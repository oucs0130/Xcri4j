/**
 * Copyright (c) 2011 University of Bolton
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this 
 * software and associated documentation files (the "Software"), to deal in the Software 
 * without restriction, including without limitation the rights to use, copy, modify, 
 * merge, publish, distribute, sublicense, and/or sell copies of the Software, and to 
 * permit persons to whom the Software is furnished to do so, subject to the following 
 * conditions:
 * The above copyright notice and this permission notice shall be included in all copies 
 * or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, 
 * INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR 
 * PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE 
 * LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, 
 * TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE 
 * OR OTHER DEALINGS IN THE SOFTWARE.
 */

package org.xcri.core;

import java.util.ArrayList;
import java.util.Date;

import javax.xml.bind.DatatypeConverter;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.Namespace;
import org.xcri.Namespaces;
import org.xcri.exceptions.InvalidElementException;
import org.xcri.types.CommonType;
import org.xcri.util.lax.Lax;

public class Catalog extends CommonType{

	private Log log = LogFactory.getLog(Catalog.class);

	private Date generated;
	private Provider[] providers;
	
	public void fromXml(Document document) throws InvalidElementException{
		this.fromXml(document.getRootElement());
	}
	
	/* (non-Javadoc)
	 * @see org.xcri.types.Common#toXml()
	 */
	@Override
	public Element toXml() {
		Element element = super.toXml();
		if (this.getProviders() != null) for (Provider provider: this.getProviders()) element.addContent(provider.toXml());		
		if (this.getGenerated() != null) element.setAttribute("generated", this.getGenerated().toString());
		return element;
	}

	/* (non-Javadoc)
	 * @see org.xcri.types.Common#fromXml(org.jdom.Element)
	 */
	@Override
	public void fromXml(Element element) throws InvalidElementException {
		super.fromXml(element);

		//
		// Add generated
		//
		if (element.getAttribute("generated")!= null){
			try {
				Date date = new Date();
				date = DatatypeConverter.parseDateTime(element.getAttributeValue("generated")).getTime();
				this.setGenerated(date);

				//
				// Time and Year?
				//
				if (!element.getAttributeValue("generated").contains("T") || element.getAttributeValue("generated").split("T")[1].length()!=9){
					log.warn("catalog: @generated contains date but not time:"+element.getAttributeValue("generated"));
				}

			} catch (Exception e) {

				log.error("catalog: @generated contains invalid date:"+element.getAttributeValue("generated"));
				throw new InvalidElementException("catalog: @generated contains invalid date:"+element.getAttributeValue("generated"));
			}
		} else {
			this.setGenerated(new Date());
		}

		//
		// Add children
		//
		ArrayList<Provider> providers = new ArrayList<Provider>();
		for (Element providerElement : Lax.getChildrenQuietly(element, "provider", Namespaces.XCRI_NAMESPACE_NS, log)){
			Provider provider = new Provider();
			provider.fromXml(providerElement);
			provider.setParent(this);
			providers.add(provider);
		}
		this.setProviders(providers.toArray(new Provider[providers.size()]));
	}

	/* (non-Javadoc)
	 * @see org.xcri.types.XcriElement#getNamespace()
	 */
	@Override
	public Namespace getNamespace() {
		return Namespaces.XCRI_NAMESPACE_NS;
	}

	/* (non-Javadoc)
	 * @see org.xcri.types.XcriElement#getName()
	 */
	@Override
	public String getName() {
		return "catalog";
	}

	/**
	 * @return the generated
	 */
	public Date getGenerated() {
		return generated;
	}

	/**
	 * @param generated the generated to set
	 */
	public void setGenerated(Date generated) {
		this.generated = generated;
	}

	/**
	 * @return the providers
	 */
	public Provider[] getProviders() {
		return providers;
	}

	/**
	 * @param providers the providers to set
	 */
	public void setProviders(Provider[] providers) {
		this.providers = providers;
	}



}
