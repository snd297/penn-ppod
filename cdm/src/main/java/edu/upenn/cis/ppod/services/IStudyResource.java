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
package edu.upenn.cis.ppod.services;

import java.util.Set;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;

import org.jboss.resteasy.annotations.GZIP;

import com.google.inject.ImplementedBy;

import edu.upenn.cis.ppod.dto.PPodLabelAndId;
import edu.upenn.cis.ppod.dto.PPodStudy;
import edu.upenn.cis.ppod.dto.StudyInfo;
import edu.upenn.cis.ppod.thirdparty.util.Pretty;

/**
 * @author Sam Donnelly
 */
@Path("/studies")
public interface IStudyResource {

	/**
	 * Create a study in the database.
	 * 
	 * @param study to be created
	 * @return a {@link StudyInfo} populated with information that resulted form
	 *         created the study
	 */
	@POST
	@Pretty
	@GZIP
	@Consumes("application/xml")
	@Produces("application/xml")
	StudyInfo createStudy(@Pretty @GZIP PPodStudy study);

	/**
	 * Get the {@link Study} that has the given pPOD ID.
	 * 
	 * @param pPodId the pPOD ID of the {@link Study} we're interested in
	 * @return the {@link Study} that has the given pPOD ID
	 */
	@GET
	@Pretty
	@GZIP
	@Path("{pPodId}")
	@Consumes("text/plain")
	@Produces("application/xml")
	PPodStudy getStudyByPPodId(@PathParam("pPodId") String pPodId);

	/**
	 * Get a set of (study pPOD ID, study label) pairs that has a member for
	 * every study in the database.
	 * 
	 * @return a set of (study pPOD ID, study label) pairs that has a member for
	 *         every study in the database
	 */
	@GET
	@Pretty
	@GZIP
	@Path("ppodidlabelpairs")
	@Produces("application/xml")
	Set<PPodLabelAndId> getStudyPPodIdLabelPairs();

	@PUT
	@Pretty
	@GZIP
	@Path("{pPodId}")
	@Consumes("application/xml")
	@Produces("application/xml")
	StudyInfo updateStudy(
			@Pretty @GZIP PPodStudy study,
			@PathParam("pPodId") String pPodId);
}