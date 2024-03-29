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

import javax.ws.rs.FormParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;

import org.jboss.resteasy.annotations.GZIP;

import edu.upenn.cis.ppod.dto.Counts;
import edu.upenn.cis.ppod.dto.PPodEntities;
import edu.upenn.cis.ppod.thirdparty.util.Pretty;

/**
 * @author Sam Donnelly
 */
@Path("/otusets")
public interface IPPodEntitiesResource {

	@POST
	@Pretty
	@GZIP
	@Path("/query")
	@Produces("application/xml")
	PPodEntities getEntitiesByHqlQuery(@FormParam("query") String query);

	@POST
	@Pretty
	@Path("/query-count")
	@Produces("application/xml")
	Counts countHqlQuery(
			@FormParam("query") String query,
			@FormParam("timeoutSeconds") Integer timeoutSeconds);

}
