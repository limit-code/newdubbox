/*
 * Copyright 1999-2011 Alibaba Group.
 *  
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *  
 *      http://www.apache.org/licenses/LICENSE-2.0
 *  
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.alibaba.dubbo.rpc.cluster.support;

import java.util.ArrayList;
import java.util.List;

import com.alibaba.dubbo.common.Constants;
import com.alibaba.dubbo.common.URL;
import com.alibaba.dubbo.common.Version;
import com.alibaba.dubbo.common.extension.ExtensionLoader;
import com.alibaba.dubbo.common.logger.Logger;
import com.alibaba.dubbo.common.logger.LoggerFactory;
import com.alibaba.dubbo.common.utils.NetUtils;
import com.alibaba.dubbo.rpc.Invocation;
import com.alibaba.dubbo.rpc.Invoker;
import com.alibaba.dubbo.rpc.Result;
import com.alibaba.dubbo.rpc.RpcException;
import com.alibaba.dubbo.rpc.cluster.Directory;
import com.alibaba.dubbo.rpc.cluster.LoadBalance;
import com.alibaba.dubbo.rpc.support.RpcUtils;

/**
 * AbstractClusterInvoker
 * 
 * @author william.liangf
 * @author chao.liuc
 */
public abstract class AbstractClusterInvoker<T> implements Invoker<T> {

    private static final Logger                logger                            = LoggerFactory
                                                                                         .getLogger(AbstractClusterInvoker.class);
    protected final Directory<T>               directory;

    protected final boolean                    availablecheck;
    
    private volatile boolean                   destroyed = false;

    private volatile Invoker<T>                stickyInvoker                     = null;

    public AbstractClusterInvoker(Directory<T> directory) {
        this(directory, directory.getUrl());
    }
    
    public AbstractClusterInvoker(Directory<T> directory, URL url) {
        if (directory == null)
            throw new IllegalArgumentException("service directory == null");
        
        this.directory = directory ;
        //sticky 需要检测 avaliablecheck 
        this.availablecheck = url.getParameter(Constants.CLUSTER_AVAILABLE_CHECK_KEY, Constants.DEFAULT_CLUSTER_AVAILABLE_CHECK) ;
    }

    public Class<T> getInterface() {
        return directory.getInterface();
    }

    public URL getUrl() {
        return directory.getUrl();
    }

    public boolean isAvailable() {
        Invoker<T> invoker = stickyInvoker;
        if (invoker != null) {
            return invoker.isAvailable();
        }
        return directory.isAvailable();
    }

    public void destroy() {
        directory.destroy();
        destroyed = true;
    }

    /**
     * 使用loadbalance选择invoker.</br>
     * a)先lb选择，如果在selected列表中 或者 不可用且做检验时，进入下一步(重选),否则直接返回</br>
     * b)重选验证规则：selected > available .保证重选出的结果尽量不在select中，并且是可用的 
     * 
     * @param availablecheck 如果设置true，在选择的时候先选invoker.available == true
     * @param selected 已选过的invoker.注意：输入保证不重复
     * 
     */
    protected Invoker<T> select(LoadBalance loadbalance, Invocation invocation, List<Invoker<T>> invokers, List<Invoker<T>> selected) throws RpcException {
        if (invokers == null || invokers.size() == 0)
            return null;
        String methodName = invocation == null ? "" : invocation.getMethodName();
        
        boolean sticky = invokers.get(0).getUrl().getMethodParameter(methodName,Constants.CLUSTER_STICKY_KEY, Constants.DEFAULT_CLUSTER_STICKY) ;
        {
            //ignore overloaded method
            if ( stickyInvoker != null && !invokers.contains(stickyInvoker) ){
                stickyInvoker = null;
            }
            //ignore cucurrent problem
            if (sticky && stickyInvoker != null && (selected == null || !selected.contains(stickyInvoker))){
                if (availablecheck && stickyInvoker.isAvailable()){
                    return stickyInvoker;
                }
            }
        }
        Invoker<T> invoker = doselect(loadbalance, invocation, invokers, selected);
        
        if (sticky){
            stickyInvoker = invoker;
        }
        return invoker;
    }
    
    private Invoker<T> doselect(LoadBalance loadbalance, Invocation invocation, List<Invoker<T>> invokers, List<Invoker<T>> selected) throws RpcException {
        if (invokers == null || invokers.size() == 0)
            return null;
        if (invokers.size() == 1)
            return invokers.get(0);
        // 如果只有两个invoker，退化成轮循
        if (invokers.size() == 2 && selected != null && selected.size() > 0) {
            return selected.get(0) == invokers.get(0) ? invokers.get(1) : invokers.get(0);
        }
        Invoker<T> invoker = loadbalance.select(invokers, getUrl(), invocation);
        
        //如果 selected中包含（优先判断） 或者 不可用&&availablecheck=true 则重试.
        if( (selected != null && selected.contains(invoker))
                ||(!invoker.isAvailable() && getUrl()!=null && availablecheck)){
            try{
                Invoker<T> rinvoker = reselect(loadbalance, invocation, invokers, selected, availablecheck);
                if(rinvoker != null){
                    invoker =  rinvoker;
                }else{
                    //看下第一次选的位置，如果不是最后，选+1位置.
                    int index = invokers.indexOf(invoker);
                    try{
                        //最后在避免碰撞
                        invoker = index <invokers.size()-1?invokers.get(index+1) :invoker;
                    }catch (Exception e) {
                        logger.warn(e.getMessage()+" may because invokers list dynamic change, ignore.",e);
                    }
                }
            }catch (Throwable t){
                logger.error("clustor relselect fail reason is :"+t.getMessage() +" if can not slove ,you can set cluster.availablecheck=false in url",t);
            }
        }
        return invoker;
    } 
    
    /**
     * 重选，先从非selected的列表中选择，没有在从selected列表中选择.
     * @param loadbalance
     * @param invocation
     * @param invokers
     * @param selected
     * @return
     * @throws RpcException
     */
    private Invoker<T> reselect(LoadBalance loadbalance,Invocation invocation,
                                List<Invoker<T>> invokers, List<Invoker<T>> selected ,boolean availablecheck)
            throws RpcException {
        
        //预先分配一个，这个列表是一定会用到的.
        List<Invoker<T>> reselectInvokers = new ArrayList<Invoker<T>>(invokers.size()>1?(invokers.size()-1):invokers.size());
        
        //先从非select中选
        if( availablecheck ){ //选isAvailable 的非select
            for(Invoker<T> invoker : invokers){
                if(invoker.isAvailable()){
                    if(selected ==null || !selected.contains(invoker)){
                        reselectInvokers.add(invoker);
                    }
                }
            }
            if(reselectInvokers.size()>0){
                return  loadbalance.select(reselectInvokers, getUrl(), invocation);
            }
        }else{ //选全部非select
            for(Invoker<T> invoker : invokers){
                if(selected ==null || !selected.contains(invoker)){
                    reselectInvokers.add(invoker);
                }
            }
            if(reselectInvokers.size()>0){
                return  loadbalance.select(resele��� �c�����6zi  2                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                        