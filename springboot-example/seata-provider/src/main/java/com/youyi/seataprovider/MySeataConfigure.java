package com.youyi.seataprovider;

import io.seata.common.util.StringUtils;
import io.seata.core.context.RootContext;
import io.seata.core.exception.TransactionException;
import io.seata.core.model.BranchType;
import io.seata.integration.http.JakartaTransactionPropagationInterceptor;
import io.seata.integration.http.TransactionPropagationInterceptor;
import io.seata.tm.api.GlobalTransactionContext;
import org.apache.shardingsphere.transaction.base.seata.at.SeataTransactionHolder;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.interceptor.TransactionInterceptor;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class MySeataConfigure implements WebMvcConfigurer {
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(new ShardingSphereInterceptor());
    }

    static class ShardingSphereInterceptor implements HandlerInterceptor {
        public boolean preHandle(jakarta.servlet.http.HttpServletRequest request, jakarta.servlet.http.HttpServletResponse response, Object handler) throws TransactionException {
            String xid = RootContext.getXID();
            if (xid == null) {
                xid = request.getHeader(RootContext.KEY_XID);
            }
            if (!StringUtils.isBlank(xid) && SeataTransactionHolder.get() == null) {
                RootContext.bind(xid);
                SeataTransactionHolder.set(GlobalTransactionContext.getCurrentOrCreate());
            }
            return true;
        }
    }
}
