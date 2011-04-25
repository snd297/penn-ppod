/*
 * Copyright (C) 2011 Trustees of the University of Pennsylvania
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package edu.upenn.cis.ppod.services;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.HashSet;

import org.hibernate.Session;
import org.hibernate.Transaction;
import org.testng.annotations.Test;

import edu.upenn.cis.ppod.TestGroupDefs;
import edu.upenn.cis.ppod.createorupdate.ICreateOrUpdateStudy;
import edu.upenn.cis.ppod.dao.IStudyDAO;
import edu.upenn.cis.ppod.dto.PPodLabelAndId;
import edu.upenn.cis.ppod.dto.PPodStudy;
import edu.upenn.cis.ppod.model.Study;

@Test(groups = TestGroupDefs.FAST)
public class StudyResourceHibernateTest {

	@Test
	public void createStudyCommitsAndCloses() {

		IStudyDAO studyDAO = mock(IStudyDAO.class);
		ICreateOrUpdateStudy createOrUpdateStudy = mock(ICreateOrUpdateStudy.class);
		Session session = mock(Session.class);
		Transaction trx = mock(Transaction.class);

		Study study = new Study();
		when(createOrUpdateStudy
				.createOrUpdateStudy(any(PPodStudy.class))).thenReturn(study);

		when(session.beginTransaction()).thenReturn(trx);

		StudyResourceHibernate studyResource =
				new StudyResourceHibernate(
						studyDAO, createOrUpdateStudy, session);
		studyResource.createStudy(new PPodStudy("dont-care", "dont-care"));

		verify(trx).commit();
		verify(session).close();
	}

	@Test
	public void getStudyByPPodIdCommitsAndCloses() {
		IStudyDAO studyDAO = mock(IStudyDAO.class);
		ICreateOrUpdateStudy createOrUpdateStudy = mock(ICreateOrUpdateStudy.class);
		Session session = mock(Session.class);
		Transaction trx = mock(Transaction.class);

		Study study = new Study();
		study.setLabel("dont-care");
		when(studyDAO.getStudyByPPodId(anyString())).thenReturn(study);

		when(session.beginTransaction()).thenReturn(trx);

		StudyResourceHibernate studyResource =
				new StudyResourceHibernate(
						studyDAO, createOrUpdateStudy, session);
		studyResource.getStudyByPPodId("don't-care");

		verify(trx).commit();
		verify(session).close();
	}

	@Test
	public void getStudyPPodIdLabelPairsCommitsAndCloses() {
		IStudyDAO studyDAO = mock(IStudyDAO.class);
		ICreateOrUpdateStudy createOrUpdateStudy = mock(ICreateOrUpdateStudy.class);
		Session session = mock(Session.class);
		Transaction trx = mock(Transaction.class);

		Study study = new Study();
		study.setLabel("dont-care");
		when(studyDAO.getPPodIdLabelPairs()).thenReturn(
				new HashSet<PPodLabelAndId>());

		when(session.beginTransaction()).thenReturn(trx);

		StudyResourceHibernate studyResource =
				new StudyResourceHibernate(
						studyDAO, createOrUpdateStudy, session);
		studyResource.getStudyPPodIdLabelPairs();

		verify(trx).commit();
		verify(session).close();
	}

	@Test
	public void updateStudy() {

		IStudyDAO studyDAO = mock(IStudyDAO.class);
		ICreateOrUpdateStudy createOrUpdateStudy = mock(ICreateOrUpdateStudy.class);
		Session session = mock(Session.class);
		Transaction trx = mock(Transaction.class);

		Study study = new Study();
		when(createOrUpdateStudy
				.createOrUpdateStudy(any(PPodStudy.class))).thenReturn(study);

		when(session.beginTransaction()).thenReturn(trx);

		StudyResourceHibernate studyResource =
				new StudyResourceHibernate(
						studyDAO, createOrUpdateStudy, session);
		studyResource.updateStudy(
				new PPodStudy("dont-care", "dont-care"), "don't-care");

		verify(trx).commit();
		verify(session).close();
	}

}
