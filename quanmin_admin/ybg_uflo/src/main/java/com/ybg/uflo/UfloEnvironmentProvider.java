package com.ybg.uflo;
import org.hibernate.SessionFactory;
import org.springframework.transaction.PlatformTransactionManager;
import com.bstek.uflo.env.EnvironmentProvider;

/** @author Deament
 * @date 2016/10/31 */
public class UfloEnvironmentProvider implements EnvironmentProvider {
	
	private SessionFactory				sessionFactory;
	private PlatformTransactionManager	platformTransactionManager;
	
	@Override
	public SessionFactory getSessionFactory() {
		return sessionFactory;
	}
	
	public void setSessionFactory(SessionFactory sessionFactory) {
		this.sessionFactory = sessionFactory;
	}
	
	@Override
	public PlatformTransactionManager getPlatformTransactionManager() {
		return platformTransactionManager;
	}
	
	public void setPlatformTransactionManager(PlatformTransactionManager platformTransactionManager) {
		this.platformTransactionManager = platformTransactionManager;
	}
	
	@Override
	public String getCategoryId() {
		return null;
	}
	
	@Override
	public String getLoginUser() {
		return "anonymous";
	}
}
