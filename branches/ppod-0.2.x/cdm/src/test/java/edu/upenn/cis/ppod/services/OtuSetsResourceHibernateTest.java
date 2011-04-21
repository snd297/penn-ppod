package edu.upenn.cis.ppod.services;

import static com.google.common.collect.Lists.newArrayList;
import static org.mockito.Matchers.anyBoolean;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.testng.Assert.assertEquals;

import java.util.List;

import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.testng.annotations.Test;

import edu.upenn.cis.ppod.TestGroupDefs;
import edu.upenn.cis.ppod.dto.PPodEntities;
import edu.upenn.cis.ppod.dto.PPodOtuSet;
import edu.upenn.cis.ppod.dto.PPodStandardMatrix;
import edu.upenn.cis.ppod.model.OtuSet;
import edu.upenn.cis.ppod.model.StandardMatrix;
import edu.upenn.cis.ppod.model.Study;

@Test(groups = TestGroupDefs.FAST)
public class OtuSetsResourceHibernateTest {

	@Test
	public void oneStandardMatrix() {

		final Session s = mock(Session.class);
		final Query q = mock(Query.class);
		final Transaction trx = mock(Transaction.class);

		when(s.createQuery(anyString())).thenReturn(q);
		when(s.getTransaction()).thenReturn(trx);
		when(q.setReadOnly(anyBoolean())).thenReturn(q);

		List<Object> matrices = newArrayList();

		Study study = new Study();
		study.setLabel("study");

		OtuSet otuSet = new OtuSet();
		study.addOtuSet(otuSet);
		otuSet.setLabel("otuSet");

		StandardMatrix matrix = new StandardMatrix();

		otuSet.addStandardMatrix(matrix);
		matrix.setLabel("matrix");

		matrices.add(matrix);

		when(q.list()).thenReturn(matrices);
		when(q.uniqueResult()).thenReturn(new Object[] { 1L, 1L, 1L, 1L });

		final IOtuSetsResource pPodEntitiesResource =
				new OtuSetsResourceHibernate(s);
		final PPodEntities results =
				pPodEntitiesResource
						.getEntitiesByHqlQuery("select something from something");

		PPodOtuSet resultOtuSet = results.getOtuSets().get(0);

		assertEquals(results.getOtuSets().size(), 1);
		assertEquals(resultOtuSet.getPPodId(),
				otuSet.getPPodId());
		assertEquals(resultOtuSet.getLabel(),
				study.getLabel() + "/" + otuSet.getLabel());
		assertEquals(resultOtuSet.getStandardMatrices().size(),
				1);
		PPodStandardMatrix resultMatrix = resultOtuSet.getStandardMatrices()
				.get(0);
		assertEquals(resultMatrix.getPPodId(), matrix.getPPodId());

	}

	@Test
	public void twoDuplicateStandardMatrices() {

		final Session s = mock(Session.class);
		final Query q = mock(Query.class);
		final Transaction trx = mock(Transaction.class);

		when(s.createQuery(anyString())).thenReturn(q);
		when(s.getTransaction()).thenReturn(trx);
		when(q.setReadOnly(anyBoolean())).thenReturn(q);

		List<Object> matrices = newArrayList();

		Study study = new Study();
		study.setLabel("study");

		OtuSet otuSet = new OtuSet();
		study.addOtuSet(otuSet);
		otuSet.setLabel("otuSet");

		StandardMatrix matrix = new StandardMatrix();

		otuSet.addStandardMatrix(matrix);
		matrix.setLabel("matrix");

		matrices.add(matrix);
		matrices.add(matrix);

		when(q.list()).thenReturn(matrices);
		when(q.uniqueResult()).thenReturn(new Object[] { 1L, 1L, 1L, 1L });

		final IOtuSetsResource pPodEntitiesResource =
				new OtuSetsResourceHibernate(s);
		final PPodEntities results = pPodEntitiesResource
				.getEntitiesByHqlQuery("select something from something");

		PPodOtuSet resultOtuSet = results.getOtuSets().get(0);

		assertEquals(results.getOtuSets().size(), 1);
		assertEquals(resultOtuSet.getPPodId(),
				otuSet.getPPodId());
		assertEquals(resultOtuSet.getLabel(),
				study.getLabel() + "/" + otuSet.getLabel());
		assertEquals(resultOtuSet.getStandardMatrices().size(),
				1);
		PPodStandardMatrix resultMatrix = resultOtuSet.getStandardMatrices()
				.get(0);
		assertEquals(resultMatrix.getPPodId(), matrix.getPPodId());

	}
}
