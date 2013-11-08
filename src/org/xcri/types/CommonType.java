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

package org.xcri.types;

import java.util.ArrayList;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jdom2.Element;
import org.xcri.Extension;
import org.xcri.Namespaces;
import org.xcri.common.*;
import org.xcri.exceptions.InvalidElementException;
import org.xcri.util.lax.Lax;

public abstract class CommonType extends XcriElement {
	
	private Log log = LogFactory.getLog(CommonType.class);
	
	/* (non-Javadoc)
	 * @see org.xcri.types.XcriElement#toXml()
	 */
	@Override
	public Element toXml() {
		
		Element element = super.toXml();
		if (this.getContributors() != null) for (Contributor c: this.getContributors()) element.addContent(c.toXml());
		if (this.getDescriptions() != null) for (Description d: this.getDescriptions()) element.addContent(d.toXml());
		if (this.getIdentifiers() != null) for (Identifier i: this.getIdentifiers()) element.addContent(i.toXml());
		if (this.getTitles() != null) for (Title t: this.getTitles()) element.addContent(t.toXml());
		if (this.getSubjects() != null) for (Subject s: this.getSubjects()) element.addContent(s.toXml());
		if (this.getImages() != null) for (Image i: this.getImages()) element.addContent(i.toXml());
		if (this.getTypes() != null) for (Type t: this.getTypes()) element.addContent(t.toXml());
		if (this.getUrls() != null) for (Url u: this.getUrls()) element.addContent(u.toXml());
		return element;
	}
	
	/* (non-Javadoc)
	 * @see org.xcri.types.XcriElement#fromXml(org.jdom.Element)
	 */
	@Override
	public void fromXml(Element element) throws InvalidElementException {
		super.fromXml(element);
		
		//
		// Check for "date" and other non-recommended elements
		//
		if (Lax.getChildQuietly(element, "date", Namespaces.DC_NAMESPACE_NS, log)!=null){
			log.warn("date: Producers SHOULD NOT use the <date> element, but instead where possible use the <start> element and the temporal elements defined in this document: <end>, <applyFrom>, and <applyUntil");
		}
		if (Lax.getChildrenQuietly(element, "hasPart", Namespaces.DC_NAMESPACE_NS, log).size()>0 || Lax.getChildrenQuietly(element, "isPartOf", Namespaces.DC_NAMESPACE_NS, log).size()>0){
			log.warn("hasPart/isPartOf: these elements are included for compatibility with the [EN 15982] standard. Producers SHOULD NOT use these elements");
		}
		
		// Process child elements
		
		ArrayList<Contributor> contributors = new ArrayList<Contributor>();
		for (Object obj: Lax.getChildrenQuietly(element, "contributor", Namespaces.DC_NAMESPACE_NS, log)){
			Contributor contributor = new Contributor();
			try {
				contributor.fromXml((Element)obj);
				contributors.add(contributor);
				
				if(contributor.getType() == null){
					log.info("contributor : Producers SHOULD use refinements of this element, for example for \"presenter\" or \"lecturer\" or other contributor types relevant to  the type of course or presentation.");
				}
				
			} catch (InvalidElementException e) {
				log.warn(this.getName()+" : skipping invalid contributor element: "+e.getMessage());
			}
		}
		this.setContributors(contributors.toArray(new Contributor[contributors.size()]));
		
		ArrayList<Description> descriptions = new ArrayList<Description>();
		for (Object obj: Lax.getChildrenQuietly(element, "description", Namespaces.DC_NAMESPACE_NS, log)){
			Description description = new Description();
			try {
				description.fromXml((Element)obj);
				descriptions.add(description);
			} catch (InvalidElementException e) {
				log.warn(this.getName()+" : skipping invalid description element: "+e.getMessage());
			}
		}
		this.setDescriptions(descriptions.toArray(new Description[descriptions.size()]));
		
		ArrayList<Identifier> identifiers = new ArrayList<Identifier>();
		for (Object obj: Lax.getChildrenQuietly(element, "identifier", Namespaces.DC_NAMESPACE_NS, log)){
			Identifier identifier = new Identifier();
			try {
				identifier.fromXml((Element)obj);
				identifiers.add(identifier);
			} catch (InvalidElementException e) {
				log.warn(this.getName()+" : skipping invalid identifier element: "+e.getMessage());
			}
		}
		this.setIdentifiers(identifiers.toArray(new Identifier[identifiers.size()]));
		
		ArrayList<Title> titles = new ArrayList<Title>();
		for (Object obj: Lax.getChildrenQuietly(element, "title", Namespaces.DC_NAMESPACE_NS, log)){
			Title title = new Title();
			try {
				title.fromXml((Element)obj);
				titles.add(title);
			} catch (InvalidElementException e) {
				log.warn(this.getName()+" : skipping invalid title element: "+e.getMessage());
			}
		}
		//
		// Check occurrences per language
		//
		ArrayList<String> languages = new ArrayList<String>();
		for(Title title: titles){
			if (title.getLang() != null && languages.contains(title.getLang())){
				log.warn("title : there SHOULD NOT be more than one occurrence of title per language tag."); 
			} else {
				languages.add(title.getLang());
			}
		}
		this.setTitles(titles.toArray(new Title[titles.size()]));
		
		ArrayList<Subject> subjects = new ArrayList<Subject>();
		for (Object obj: Lax.getChildrenQuietly(element, "subject", Namespaces.DC_NAMESPACE_NS, log)){
			Subject subject = new Subject();
			try {
				subject.fromXml((Element)obj);
				subjects.add(subject);
			} catch (InvalidElementException e) {
				log.warn(this.getName()+" : skipping invalid subject element: "+e.getMessage());
			}
		}
		this.setSubjects(subjects.toArray(new Subject[subjects.size()]));
		
		ArrayList<Image> images = new ArrayList<Image>();
		for (Object obj:Lax.getChildrenQuietly(element, "image", Namespaces.XCRI_NAMESPACE_NS, log)){
			Image image = new Image();
			try {
				image.fromXml((Element)obj);
				
				//
				// Check for alternate text
				//
				if (image.getAlt() == null||image.getAlt().length() == 0){
					log.warn("image: While @alt is optional, following the structure of XHTML, a Producer SHOULD provide meaningful alternative text");
				}
				
				//
				// Check for known image type
				//
				String type = image.getSrc().substring(image.getSrc().lastIndexOf(".")+1, image.getSrc().length());
				if (!type.equals("png") && !type.equals("jpg") && !type.equals("gif")){
					log.warn("image : A Producer SHOULD offer images in standard formats, such as PNG and JPEG");
				}
				images.add(image);
			} catch (InvalidElementException e) {
				log.warn(this.getName()+" : skipping invalid image element: "+e.getMessage());
			}
		}
		this.setImages(images.toArray(new Image[images.size()]));
		
		ArrayList<Type> types = new ArrayList<Type>();
		for (Object obj: Lax.getChildrenQuietly(element, "type", Namespaces.DC_NAMESPACE_NS, log)){
			Type type = new Type();
			try {
				type.fromXml((Element)obj);
				types.add(type);
			} catch (InvalidElementException e) {
				log.warn(this.getName()+" : skipping invalid type element: "+e.getMessage());
			}
		}
		this.setTypes(types.toArray(new Type[types.size()]));
		
		ArrayList<Url> urls = new ArrayList<Url>();
		for (Object obj: Lax.getChildrenQuietly(element, "url", Namespaces.MLO_NAMESPACE_NS, log)){
			Url url = new Url();
			try {
				url.fromXml((Element)obj);
				urls.add(url);
			} catch (InvalidElementException e) {
				log.error(e.getMessage());
			}
		}
		this.setUrls(urls.toArray(new Url[urls.size()]));
		
		//
		// Check custom extensions
		//
		ArrayList<Extension> extensions = new ArrayList<Extension>();
		for (Extension extension : ExtensionManager.getExtensions()) {
			for (Element obj : Lax.getChildrenQuietly(
					element, extension.getName(), extension.getNamespace(), log)) {
				
				try {
					Extension myExtension = extension.getClass().newInstance();
					myExtension.fromXml(obj);
					extensions.add(myExtension);
				} catch (InvalidElementException e) {
					log.error(e.getMessage());
				} catch (InstantiationException e) {
					log.error(e.getMessage());
				} catch (IllegalAccessException e) {
					log.error(e.getMessage());
				}
				extension.fromXml(obj);
			}
		}
		this.setExtensions(extensions.toArray(new Extension[extensions.size()]));
		
	}
	
	private Contributor[] contributors;
	private Description[] descriptions;
	private Extension[] extensions;
	private Identifier[] identifiers;
	protected Title[] titles;
	private Subject[] subjects;
	private Image[] images;
	private Type[] types;
	private Url[] urls;
	
	/**
	 * @return the contributors
	 */
	public Contributor[] getContributors() {
		return contributors;
	}
	/**
	 * @param contributors the contributors to set
	 */
	public void setContributors(Contributor[] contributors) {
		this.contributors = contributors;
	}
	/**
	 * @return the descriptions
	 */
	public Description[] getDescriptions() {
		if (descriptions == null || descriptions.length == 0){
			if (this.getParent() instanceof CommonType){
				Description[] parentDescriptions = ((CommonType)this.getParent()).getDescriptions();
				this.descriptions = new Description[parentDescriptions.length];
				for (int i=0;i<parentDescriptions.length;i++){
					this.descriptions[i] = parentDescriptions[i].clone();
				}
				//this.setDescriptions(((CommonType)this.getParent()).getDescriptions().clone());
				//return ((CommonType)this.getParent()).getDescriptions();
			}
		} 
		return descriptions;
	}
	/**
	 * Inheritable
	 * @param descriptions the descriptions to set
	 */
	public void setDescriptions(Description[] descriptions) {
		this.descriptions = descriptions;
	}
	
	/**
	 * @return the extensions
	 */
	public Extension[] getExtensions() {
		return extensions;
	}
	/**
	 * @param extentions the extensions to set
	 */
	public void setExtensions(Extension[] extensions) {
		this.extensions = extensions;
	}
	
	/**
	 * @return the identifiers
	 */
	public Identifier[] getIdentifiers() {
		return identifiers;
	}
	/**
	 * @param identifiers the identifiers to set
	 */
	public void setIdentifiers(Identifier[] identifiers) {
		this.identifiers = identifiers;
	}
	/**
	 * @return the titles
	 */
	public Title[] getTitles() {
		return titles;
	}
	/**
	 * @param titles the titles to set
	 */
	public void setTitles(Title[] titles) {
		this.titles = titles;
	}
	/**
	 * Inheritable
	 * @return the subjects
	 */
	public Subject[] getSubjects() {
		if (subjects == null || subjects.length == 0){
			if (this.getParent() instanceof CommonType){
				return ((CommonType)this.getParent()).getSubjects();
			}
		} 
		return subjects;
	}
	/**
	 * @param subjects the subjects to set
	 */
	public void setSubjects(Subject[] subjects) {
		this.subjects = subjects;
	}
	/**
	 * Inheritable
	 * @return the images
	 */
	public Image[] getImages() {
		if (images == null || images.length == 0){
			if (this.getParent() instanceof CommonType){
				return ((CommonType)this.getParent()).getImages();
			}
		} 
		return images;
	}
	/**
	 * @param images the images to set
	 */
	public void setImages(Image[] images) {
		this.images = images;
	}
	/**
	 * @return the types
	 */
	public Type[] getTypes() {
		return types;
	}
	/**
	 * @param types the types to set
	 */
	public void setTypes(Type[] types) {
		this.types = types;
	}
	/**
	 * @return the urls 
	 */
	public Url[] getUrls() {
		return urls;
	}
	/**
	 * @param urls the urls to set
	 */
	public void setUrls(Url[] urls) {
		this.urls = urls;
	}
	
}
