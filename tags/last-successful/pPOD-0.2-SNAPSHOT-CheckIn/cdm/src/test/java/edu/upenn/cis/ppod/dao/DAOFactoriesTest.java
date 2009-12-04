/*
 * Copyright (C) 2009 Trustees of the University of Pennsylvania
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS of ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package edu.upenn.cis.ppod.dao;

import org.testng.annotations.Test;

/**
 * @author Sam Donnelly
 * 
 */
public class DAOFactoriesTest {

	private static class Factory implements IDAOFactory {

		/*
		 * (non-Javadoc)
		 * 
		 * @see edu.upenn.cis.ppod.dao.IDAOFactory#getMatrixDAO()
		 */
		public ICharacterStateMatrixDAO geCharStateMatrixDAO() {
			// TODO Auto-generated method stub
			return null;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see edu.upenn.cis.ppod.dao.IDAOFactory#getOTUDAO()
		 */
		public IOTUDAO getOTUDAO() {
			// TODO Auto-generated method stub
			return null;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see edu.upenn.cis.ppod.dao.IDAOFactory#getOTUSetDAO()
		 */
		public IOTUSetDAO getOTUSetDAO() {
			// TODO Auto-generated method stub
			return null;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see edu.upenn.cis.ppod.dao.IDAOFactory#getPPodVersionInfoDAO()
		 */
		public IPPodVersionInfoDAO getPPodVersionInfoDAO() {
			// TODO Auto-generated method stub
			return null;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see edu.upenn.cis.ppod.dao.IDAOFactory#getPhyloCharDAO()
		 */
		public ICharacterDAO getCharacterDAO() {
			// TODO Auto-generated method stub
			return null;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see edu.upenn.cis.ppod.dao.IDAOFactory#getPhyloCharStateDAO()
		 */
		public ICharacterStateDAO getCharacterStateDAO() {
			// TODO Auto-generated method stub
			return null;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see edu.upenn.cis.ppod.dao.IDAOFactory#getRowDAO()
		 */
		public ICharacterStateRowDAO getCharacterStateRowDAO() {
			// TODO Auto-generated method stub
			return null;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see edu.upenn.cis.ppod.dao.IDAOFactory#getStudyDAO()
		 */
		public IStudyDAO getStudyDAO() {
			// TODO Auto-generated method stub
			return null;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see edu.upenn.cis.ppod.dao.IDAOFactory#getTreeDAO()
		 */
		public ITreeDAO getTreeDAO() {
			// TODO Auto-generated method stub
			return null;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see edu.upenn.cis.ppod.dao.IDAOFactory#getTreeSetDAO()
		 */
		public ITreeSetDAO getTreeSetDAO() {
			// TODO Auto-generated method stub
			return null;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see edu.upenn.cis.ppod.dao.IDAOFactory#getAttachmentDAO()
		 */
		public IAttachmentDAO getAttachmentDAO() {
			// TODO Auto-generated method stub
			return null;
		}

		/* (non-Javadoc)
		 * @see edu.upenn.cis.ppod.dao.IDAOFactory#getAttachmentTypeDAO()
		 */
		public IAttachmentTypeDAO getAttachmentTypeDAO() {
			// TODO Auto-generated method stub
			return null;
		}

		/* (non-Javadoc)
		 * @see edu.upenn.cis.ppod.dao.IDAOFactory#getAttachmentNamespaceDAO()
		 */
		public IAttachmentNamespaceDAO getAttachmentNamespaceDAO() {
			// TODO Auto-generated method stub
			return null;
		}

		/* (non-Javadoc)
		 * @see edu.upenn.cis.ppod.dao.IDAOFactory#getUserDAO()
		 */
		public IUserDAO getPPodUserDAO() {
			// TODO Auto-generated method stub
			return null;
		}

		/* (non-Javadoc)
		 * @see edu.upenn.cis.ppod.dao.IDAOFactory#getPPodGroupDAO()
		 */
		public IPPodGroupDAO getPPodGroupDAO() {
			// TODO Auto-generated method stub
			return null;
		}

		/* (non-Javadoc)
		 * @see edu.upenn.cis.ppod.dao.IDAOFactory#getPPodRoleDAO()
		 */
		public IPPodRoleDAO getPPodRoleDAO() {
			// TODO Auto-generated method stub
			return null;
		}

	}

	@Test
	public void test() {
		IDAOFactory factory = null;   //new PPodFactory().create(Factory.class);
		System.out.println("factory: " + factory);
		try {
			AClass.meth();
		} catch (Throwable t) {
			t.printStackTrace();
		}
		System.out.println("got to end");
	}
}

class AClass {

	static {
		try {
			if (true)
				throw new ArrayIndexOutOfBoundsException();
		} catch (Throwable t) {
			throw new ExceptionInInitializerError(t);
		}
	}

	static void meth() {
		System.out.println("meth");
	}

}
