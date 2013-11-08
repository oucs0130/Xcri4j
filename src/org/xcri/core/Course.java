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

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jdom2.Element;
import org.jdom2.Namespace;
import org.xcri.Namespaces;
import org.xcri.common.Identifier;
import org.xcri.course.Credit;
import org.xcri.course.Qualification;
import org.xcri.exceptions.InvalidElementException;
import org.xcri.factory.PresentationFactory;
import org.xcri.types.CommonDescriptiveType;
import org.xcri.util.lax.Lax;

public class Course extends CommonDescriptiveType {

	private Log log = LogFactory.getLog(Course.class);

	private Presentation[] presentations;
	private Qualification[] qualifications;
	private Credit[] credits;
	// Note we ignore Level as it isn't used by XCRI

	/**
	 * @return the presentations
	 */
	public Presentation[] getPresentations() {
		return presentations;
	}

	/**
	 * @param presentations the presentations to set
	 */
	public void setPresentations(Presentation[] presentations) {
		this.presentations = presentations;
	}

	/* (non-Javadoc)
	 * @see org.xcri.types.CommonType#toXml()
	 */
	@Override
	public Element toXml() {
		Element element = super.toXml();
		if (this.getPresentations()!= null) for (Presentation presentation:this.getPresentations()) element.addContent(presentation.toXml());
		if (this.getQualifications()!= null) for (Qualification qualification:this.getQualifications()) element.addContent(qualification.toXml());
		if (this.getCredits()!= null) for (Credit credit:this.getCredits()) element.addContent(credit.toXml());
		return element;
	}

	/* (non-Javadoc)
	 * @see org.xcri.types.CommonType#fromXml(org.jdom.Element)
	 */
	@Override
	public void fromXml(Element element) throws InvalidElementException {
		super.fromXml(element);

		//
		// Check identifiers
		//
		if (this.getIdentifiers() == null || this.getIdentifiers().length == 0){
			log.warn("course: course does not contain any identifiers");
		} else {
			boolean hasUrl = false;
			for (Identifier identifier: this.getIdentifiers()){
				try {
					new URL(identifier.getValue());
					hasUrl = true;
				} catch (MalformedURLException e) {
					if (identifier.getType() == null){
						log.warn("course: course contains a non-URI identifier with no type:"+identifier.getValue());							
					}
				}
			}
			if (!hasUrl){
				log.warn("course: course does not contain a URI identifier");
			}
		}
		
		//
		// Check titles
		//
		if (this.getTitles() == null || this.getTitles().length == 0){
			log.warn("course: course has no title");
		}
		
		//
		// Check subjects
		//
		if (this.getSubjects() == null || this.getSubjects().length == 0){
			log.warn("course: course does not contain a subject");
		}
		
		//
		// Level
		//
		if (element.getChild("level", Namespaces.MLO_NAMESPACE_NS) != null){
			log.warn("course: level is not recommended");
		}

		//
		// Add children
		//
		ArrayList<Presentation> presentations = new ArrayList<Presentation>();
		for (Object obj : Lax.getChildrenQuietly(element, "presentation", Namespaces.XCRI_NAMESPACE_NS, log)){
			try {
				Presentation presentation = PresentationFactory.getPresentation(Presentation.class);
				presentation.fromXml((Element)obj);
				presentation.setParent(this);
				presentations.add(presentation);
			} catch (InvalidElementException e) {
				log.warn("course : presentation invalid, skipping");
			} catch (InstantiationException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		this.setPresentations(presentations.toArray(new Presentation[presentations.size()]));

		ArrayList<Qualification> qualifications = new ArrayList<Qualification>();
		for (Object obj : Lax.getChildrenQuietly(element, "qualification", Namespaces.MLO_NAMESPACE_NS, log)){
			Qualification qualification = new Qualification();
			try {
				qualification.fromXml((Element)obj);
				qualifications.add(qualification);
			} catch (InvalidElementException e) {
				log.warn("course : qualification invalid, skipping");
			}
		}
		this.setQualifications(qualifications.toArray(new Qualification[qualifications.size()]));

		ArrayList<Credit> credits = new ArrayList<Credit>();
		for (Object obj : Lax.getChildrenQuietly(element, "credit", Namespaces.MLO_NAMESPACE_NS, log)){
			Credit credit = new Credit();
			try {
				credit.fromXml((Element)obj);
				credits.add(credit);
			} catch (InvalidElementException e) {
				log.warn("course : credit invalid, skipping");
			}
		}
		this.setCredits(credits.toArray(new Credit[credits.size()]));


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
		return "course";
	}

	/**
	 * @return the qualifications
	 */
	public Qualification[] getQualifications() {
		return qualifications;
	}

	/**
	 * @param qualifications the qualifications to set
	 */
	public void setQualifications(Qualification[] qualifications) {
		this.qualifications = qualifications;
	}

	/**
	 * @return the credits
	 */
	public Credit[] getCredits() {
		return credits;
	}

	/**
	 * @param credits the credits to set
	 */
	public void setCredits(Credit[] credits) {
		this.credits = credits;
	}



}
