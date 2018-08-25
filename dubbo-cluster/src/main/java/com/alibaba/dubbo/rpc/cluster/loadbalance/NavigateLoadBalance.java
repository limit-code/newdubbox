package com.alibaba.dubbo.rpc.cluster.loadbalance;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import com.alibaba.dubbo.common.URL;
import com.alibaba.dubbo.rpc.Invocation;
import com.alibaba.dubbo.rpc.Invoker;
import org.apache.log4j.Logger;


public class NavigateLoadBalance extends AbstractLoadBalance {
    public static final String NAME = "navigate";
    private static Logger LOGGER = Logger.getLogger(NavigateLoadBalance.class);

    @SuppressWarnings("unchecked")
    @Override
    protected <T> Invoker<T> doSelect(List<Invoker<T>> invokers, URL url, Invocation invocation) {
        int id=0,index=0; 
        id=Integer.parseInt(invocation.getArguments()[0].toString());
        index= (int) (id % invokers.size());
        if(invokers.size()<2)
        {
        	if(LOGGER.isInfoEnabled())
            LOGGER.info("NavigateLoadBalance "+id+"  "+index+"  "+invokers.get(0).getUrl().getAddress());
            return invokers.get(0);
        }
        else
        {
            for(Invoker<T> t:invokers)
            {
                if(Integer.toString(t.getUrl().getPort()).endsWith(Integer.toString(index)))
                {
                	if(LOGGER.isInfoEnabled())
                    LOGGER.info("NavigateLoadBalance "+id+"  "+index+"  "+t.getUrl().getAddress());
                        return t;
                 }
             }
                return null;
         }
 
 



    }
 

}
