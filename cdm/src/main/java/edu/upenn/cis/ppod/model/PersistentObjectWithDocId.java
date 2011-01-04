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
package edu.upenn.cis.ppod.model;

import java.util.UUID;

import javax.annotation.Nullable;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlID;

import edu.upenn.cis.ppod.imodel.IHasDocId;

/**
 * A {@code PersistentObject} w/ an {@link XmlID} attribute called
 * {@code "docId"}.
 * <p>
 * We only made this class public because we were getting this exception when we
 * marshalled (full stack trace at bottom of file):
 * 
 * <pre>
 * Caused by: java.lang.IllegalAccessException: Class org.hibernate.proxy.pojo.javassist.JavassistLazyInitializer can not access a member of class edu.upenn.cis.ppod.model.PersistentObjectWithDocId with modifiers "public"
 * 	at sun.reflect.Reflection.ensureMemberAccess(Reflection.java:65)
 * 	at java.lang.reflect.Method.invoke(Method.java:588)
 * 	at org.hibernate.proxy.pojo.javassist.JavassistLazyInitializer.invoke(JavassistLazyInitializer.java:198)
 * 	at edu.upenn.cis.ppod.model.AttachmentType_$$_javassist_14.getXmlId(AttachmentType_$$_javassist_14.java)
 * 	at sun.reflect.GeneratedMethodAccessor101.invoke(Unknown Source)
 * 	at sun.reflect.DelegatingMethodAccessorImpl.invoke(DelegatingMethodAccessorImpl.java:25)
 * 	at java.lang.reflect.Method.invoke(Method.java:597)
 * 	at com.sun.xml.bind.v2.runtime.reflect.Accessor$GetterSetterReflection.get(Accessor.java:324)
 * 	... 82 more
 * </pre>
 * 
 * @author Sam Donnelly
 */
public abstract class PersistentObjectWithDocId
		extends PersistentObject
		implements IHasDocId {
	/**
	 * Intended for referencing elements within a document - be it XML, JSON,
	 * etc. This is distinct from the pPOD Id of {@link UUPPodEntity}.
	 */
	@Nullable
	private String docId;

	@XmlID
	@XmlAttribute
	@Nullable
	public String getDocId() {
		return docId;
	}

	public void setDocId() {
		setDocId(UUID.randomUUID().toString());
	}

	public void setDocId(final String docId) {
		if (getDocId() != null) {
			throw new IllegalStateException("docId was already set");
		}
		this.docId = docId;
	}
}

/**
 * <pre>
 *    org.jboss.resteasy.plugins.providers.jaxb.JAXBMarshalException: javax.xml.bind.MarshalException
 *  - with linked exception:
 * [com.sun.xml.bind.api.AccessorException: java.lang.IllegalAccessException: Class org.hibernate.proxy.pojo.javassist.JavassistLazyInitializer can not access a member of class edu.upenn.cis.ppod.model.PersistentObjectWithDocId with modifiers "public"]
 * 	at org.jboss.resteasy.plugins.providers.jaxb.AbstractJAXBProvider.writeTo(AbstractJAXBProvider.java:109)
 * 	at org.jboss.resteasy.core.interception.MessageBodyWriterContextImpl.proceed(MessageBodyWriterContextImpl.java:117)
 * 	at org.jboss.resteasy.plugins.interceptors.encoding.GZIPEncodingInterceptor.write(GZIPEncodingInterceptor.java:37)
 * 	at org.jboss.resteasy.core.interception.MessageBodyWriterContextImpl.proceed(MessageBodyWriterContextImpl.java:123)
 * 	at org.jboss.resteasy.plugins.interceptors.encoding.ContentEncodingHeaderInterceptor.write(ContentEncodingHeaderInterceptor.java:38)
 * 	at org.jboss.resteasy.plugins.interceptors.encoding.ServerContentEncodingHeaderInterceptor.write(ServerContentEncodingHeaderInterceptor.java:45)
 * 	at org.jboss.resteasy.core.interception.MessageBodyWriterContextImpl.proceed(MessageBodyWriterContextImpl.java:123)
 * 	at org.jboss.resteasy.core.ServerResponse.writeTo(ServerResponse.java:186)
 * 	at org.jboss.resteasy.core.SynchronousDispatcher.writeJaxrsResponse(SynchronousDispatcher.java:566)
 * 	at org.jboss.resteasy.core.SynchronousDispatcher.invoke(SynchronousDispatcher.java:487)
 * 	at org.jboss.resteasy.core.SynchronousDispatcher.invoke(SynchronousDispatcher.java:120)
 * 	at org.jboss.resteasy.plugins.server.servlet.ServletContainerDispatcher.service(ServletContainerDispatcher.java:200)
 * 	at org.jboss.resteasy.plugins.server.servlet.HttpServletDispatcher.service(HttpServletDispatcher.java:48)
 * 	at org.jboss.resteasy.plugins.server.servlet.HttpServletDispatcher.service(HttpServletDispatcher.java:43)
 * 	at javax.servlet.http.HttpServlet.service(HttpServlet.java:820)
 * 	at org.mortbay.jetty.servlet.ServletHolder.handle(ServletHolder.java:511)
 * 	at org.mortbay.jetty.servlet.ServletHandler$CachedChain.doFilter(ServletHandler.java:1166)
 * 	at edu.upenn.cis.ppod.thirdparty.util.HibernateSessionPerRequestFilter.doFilter(HibernateSessionPerRequestFilter.java:82)
 * 	at org.mortbay.jetty.servlet.ServletHandler$CachedChain.doFilter(ServletHandler.java:1157)
 * 	at org.apache.shiro.web.servlet.ProxiedFilterChain.doFilter(ProxiedFilterChain.java:62)
 * 	at org.apache.shiro.web.servlet.AdviceFilter.executeChain(AdviceFilter.java:109)
 * 	at org.apache.shiro.web.servlet.AdviceFilter.doFilterInternal(AdviceFilter.java:138)
 * 	at org.apache.shiro.web.servlet.OncePerRequestFilter.doFilter(OncePerRequestFilter.java:83)
 * 	at org.apache.shiro.web.servlet.ProxiedFilterChain.doFilter(ProxiedFilterChain.java:67)
 * 	at org.apache.shiro.web.servlet.AbstractShiroFilter.executeChain(AbstractShiroFilter.java:359)
 * 	at org.apache.shiro.web.servlet.AbstractShiroFilter$1.call(AbstractShiroFilter.java:275)
 * 	at org.apache.shiro.subject.support.SubjectCallable.doCall(SubjectCallable.java:90)
 * 	at org.apache.shiro.subject.support.SubjectCallable.call(SubjectCallable.java:83)
 * 	at org.apache.shiro.subject.support.DelegatingSubject.execute(DelegatingSubject.java:343)
 * 	at org.apache.shiro.web.servlet.AbstractShiroFilter.doFilterInternal(AbstractShiroFilter.java:272)
 * 	at org.apache.shiro.web.servlet.OncePerRequestFilter.doFilter(OncePerRequestFilter.java:83)
 * 	at org.mortbay.jetty.servlet.ServletHandler$CachedChain.doFilter(ServletHandler.java:1157)
 * 	at org.mortbay.jetty.servlet.ServletHandler.handle(ServletHandler.java:388)
 * 	at org.mortbay.jetty.security.SecurityHandler.handle(SecurityHandler.java:216)
 * 	at org.mortbay.jetty.servlet.SessionHandler.handle(SessionHandler.java:182)
 * 	at org.mortbay.jetty.handler.ContextHandler.handle(ContextHandler.java:765)
 * 	at org.mortbay.jetty.webapp.WebAppContext.handle(WebAppContext.java:418)
 * 	at org.mortbay.jetty.handler.HandlerWrapper.handle(HandlerWrapper.java:152)
 * 	at org.mortbay.jetty.Server.handle(Server.java:326)
 * 	at org.mortbay.jetty.HttpConnection.handleRequest(HttpConnection.java:542)
 * 	at org.mortbay.jetty.HttpConnection$RequestHandler.headerComplete(HttpConnection.java:923)
 * 	at org.mortbay.jetty.HttpParser.parseNext(HttpParser.java:547)
 * 	at org.mortbay.jetty.HttpParser.parseAvailable(HttpParser.java:212)
 * 	at org.mortbay.jetty.HttpConnection.handle(HttpConnection.java:404)
 * 	at org.mortbay.io.nio.SelectChannelEndPoint.run(SelectChannelEndPoint.java:409)
 * 	at org.mortbay.thread.QueuedThreadPool$PoolThread.run(QueuedThreadPool.java:582)
 * Caused by: javax.xml.bind.MarshalException
 *  - with linked exception:
 * [com.sun.xml.bind.api.AccessorException: java.lang.IllegalAccessException: Class org.hibernate.proxy.pojo.javassist.JavassistLazyInitializer can not access a member of class edu.upenn.cis.ppod.model.PersistentObjectWithDocId with modifiers "public"]
 * 	at com.sun.xml.bind.v2.runtime.MarshallerImpl.write(MarshallerImpl.java:318)
 * 	at com.sun.xml.bind.v2.runtime.MarshallerImpl.marshal(MarshallerImpl.java:244)
 * 	at javax.xml.bind.helpers.AbstractMarshallerImpl.marshal(AbstractMarshallerImpl.java:75)
 * 	at org.jboss.resteasy.plugins.providers.jaxb.AbstractJAXBProvider.writeTo(AbstractJAXBProvider.java:105)
 * 	... 45 more
 * Caused by: com.sun.xml.bind.api.AccessorException: java.lang.IllegalAccessException: Class org.hibernate.proxy.pojo.javassist.JavassistLazyInitializer can not access a member of class edu.upenn.cis.ppod.model.PersistentObjectWithDocId with modifiers "public"
 * 	at com.sun.xml.bind.v2.runtime.XMLSerializer.reportError(XMLSerializer.java:246)
 * 	at com.sun.xml.bind.v2.runtime.XMLSerializer.reportError(XMLSerializer.java:261)
 * 	at com.sun.xml.bind.v2.runtime.ClassBeanInfoImpl.getId(ClassBeanInfoImpl.java:299)
 * 	at com.sun.xml.bind.v2.runtime.reflect.TransducedAccessor$IDREFTransducedAccessorImpl.print(TransducedAccessor.java:284)
 * 	at com.sun.xml.bind.v2.runtime.reflect.TransducedAccessor$IDREFTransducedAccessorImpl.print(TransducedAccessor.java:265)
 * 	at com.sun.xml.bind.v2.runtime.property.AttributeProperty.serializeAttributes(AttributeProperty.java:97)
 * 	at com.sun.xml.bind.v2.runtime.ClassBeanInfoImpl.serializeAttributes(ClassBeanInfoImpl.java:344)
 * 	at com.sun.xml.bind.v2.runtime.XMLSerializer.childAsXsiType(XMLSerializer.java:689)
 * 	at com.sun.xml.bind.v2.runtime.property.ArrayElementNodeProperty.serializeItem(ArrayElementNodeProperty.java:65)
 * 	at com.sun.xml.bind.v2.runtime.property.ArrayElementProperty.serializeListBody(ArrayElementProperty.java:168)
 * 	at com.sun.xml.bind.v2.runtime.property.ArrayERProperty.serializeBody(ArrayERProperty.java:152)
 * 	at com.sun.xml.bind.v2.runtime.ClassBeanInfoImpl.serializeBody(ClassBeanInfoImpl.java:332)
 * 	at com.sun.xml.bind.v2.runtime.ClassBeanInfoImpl.serializeBody(ClassBeanInfoImpl.java:328)
 * 	at com.sun.xml.bind.v2.runtime.ClassBeanInfoImpl.serializeBody(ClassBeanInfoImpl.java:328)
 * 	at com.sun.xml.bind.v2.runtime.ClassBeanInfoImpl.serializeBody(ClassBeanInfoImpl.java:328)
 * 	at com.sun.xml.bind.v2.runtime.XMLSerializer.childAsXsiType(XMLSerializer.java:699)
 * 	at com.sun.xml.bind.v2.runtime.property.ArrayElementNodeProperty.serializeItem(ArrayElementNodeProperty.java:65)
 * 	at com.sun.xml.bind.v2.runtime.property.ArrayElementProperty.serializeListBody(ArrayElementProperty.java:168)
 * 	at com.sun.xml.bind.v2.runtime.property.ArrayERProperty.serializeBody(ArrayERProperty.java:152)
 * 	at com.sun.xml.bind.v2.runtime.ClassBeanInfoImpl.serializeBody(ClassBeanInfoImpl.java:332)
 * 	at com.sun.xml.bind.v2.runtime.XMLSerializer.childAsXsiType(XMLSerializer.java:699)
 * 	at com.sun.xml.bind.v2.runtime.property.ArrayElementNodeProperty.serializeItem(ArrayElementNodeProperty.java:65)
 * 	at com.sun.xml.bind.v2.runtime.property.ArrayElementProperty.serializeListBody(ArrayElementProperty.java:168)
 * 	at com.sun.xml.bind.v2.runtime.property.ArrayERProperty.serializeBody(ArrayERProperty.java:152)
 * 	at com.sun.xml.bind.v2.runtime.ClassBeanInfoImpl.serializeBody(ClassBeanInfoImpl.java:332)
 * 	at com.sun.xml.bind.v2.runtime.XMLSerializer.childAsXsiType(XMLSerializer.java:699)
 * 	at com.sun.xml.bind.v2.runtime.property.ArrayElementNodeProperty.serializeItem(ArrayElementNodeProperty.java:65)
 * 	at com.sun.xml.bind.v2.runtime.property.ArrayElementProperty.serializeListBody(ArrayElementProperty.java:168)
 * 	at com.sun.xml.bind.v2.runtime.property.ArrayERProperty.serializeBody(ArrayERProperty.java:152)
 * 	at com.sun.xml.bind.v2.runtime.ClassBeanInfoImpl.serializeBody(ClassBeanInfoImpl.java:332)
 * 	at com.sun.xml.bind.v2.runtime.XMLSerializer.childAsSoleContent(XMLSerializer.java:593)
 * 	at com.sun.xml.bind.v2.runtime.ClassBeanInfoImpl.serializeRoot(ClassBeanInfoImpl.java:320)
 * 	at com.sun.xml.bind.v2.runtime.XMLSerializer.childAsRoot(XMLSerializer.java:494)
 * 	at com.sun.xml.bind.v2.runtime.MarshallerImpl.write(MarshallerImpl.java:315)
 * 	... 48 more
 * Caused by: com.sun.xml.bind.api.AccessorException: java.lang.IllegalAccessException: Class org.hibernate.proxy.pojo.javassist.JavassistLazyInitializer can not access a member of class edu.upenn.cis.ppod.model.PersistentObjectWithDocId with modifiers "public"
 * 	at com.sun.xml.bind.v2.runtime.reflect.Accessor$GetterSetterReflection.handleInvocationTargetException(Accessor.java:357)
 * 	at com.sun.xml.bind.v2.runtime.reflect.Accessor$GetterSetterReflection.get(Accessor.java:328)
 * 	at com.sun.xml.bind.v2.runtime.reflect.TransducedAccessor$CompositeTransducedAccessorImpl.print(TransducedAccessor.java:235)
 * 	at com.sun.xml.bind.v2.runtime.property.AttributeProperty.getIdValue(AttributeProperty.java:124)
 * 	at com.sun.xml.bind.v2.runtime.ClassBeanInfoImpl.getId(ClassBeanInfoImpl.java:297)
 * 	... 79 more
 * Caused by: java.lang.IllegalAccessException: Class org.hibernate.proxy.pojo.javassist.JavassistLazyInitializer can not access a member of class edu.upenn.cis.ppod.model.PersistentObjectWithDocId with modifiers "public"
 * 	at sun.reflect.Reflection.ensureMemberAccess(Reflection.java:65)
 * 	at java.lang.reflect.Method.invoke(Method.java:588)
 * 	at org.hibernate.proxy.pojo.javassist.JavassistLazyInitializer.invoke(JavassistLazyInitializer.java:198)
 * 	at edu.upenn.cis.ppod.model.AttachmentType_$$_javassist_14.getXmlId(AttachmentType_$$_javassist_14.java)
 * 	at sun.reflect.GeneratedMethodAccessor101.invoke(Unknown Source)
 * 	at sun.reflect.DelegatingMethodAccessorImpl.invoke(DelegatingMethodAccessorImpl.java:25)
 * 	at java.lang.reflect.Method.invoke(Method.java:597)
 * 	at com.sun.xml.bind.v2.runtime.reflect.Accessor$GetterSetterReflection.get(Accessor.java:324)
 * 	... 82 more
 * </pre>
 */
