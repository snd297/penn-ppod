package edu.upenn.cis.ppod.services;

import org.hibernate.context.ManagedSessionContext;
import org.testng.annotations.Test;

import com.google.inject.Inject;

import edu.upenn.cis.ppod.TestGroupDefs;
import edu.upenn.cis.ppod.services.ppodentity.IPPodEntities;
import edu.upenn.cis.ppod.thirdparty.HibernateUtil;

/**
 * @author Sam Donnelly
 */
@Test(groups = { TestGroupDefs.FAST, TestGroupDefs.IN_DEVELOPMENT }, dependsOnGroups = TestGroupDefs.INIT)
public class PPodEntitiesResourceTest {

	@Inject
	private IPPodEntitiesResource pPodEntitiesResource;

	public void getEntitiesByHqlQuery() {
		ManagedSessionContext.bind(HibernateUtil.getSessionFactory()
				.openSession());
		final IPPodEntities entities = pPodEntitiesResource
				.getEntitiesByHqlQuery("from CharacterStateMatrix m join fetch m.otuSet os join fetch os.otus o where o.label='Sus'");
		System.out.println(entities);
		ManagedSessionContext.unbind(HibernateUtil.getSessionFactory());
	}
}
