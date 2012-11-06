/**
 * Copyright (c) 2011 Evolveum
 *
 * The contents of this file are subject to the terms
 * of the Common Development and Distribution License
 * (the License). You may not use this file except in
 * compliance with the License.
 *
 * You can obtain a copy of the License at
 * http://www.opensource.org/licenses/cddl1 or
 * CDDLv1.0.txt file in the source code distribution.
 * See the License for the specific language governing
 * permission and limitations under the License.
 *
 * If applicable, add the following below the CDDL Header,
 * with the fields enclosed by brackets [] replaced by
 * your own identifying information:
 * Portions Copyrighted 2011 [name of copyright owner]
 */
package com.evolveum.midpoint.prism.path;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.xml.namespace.QName;

import com.evolveum.midpoint.util.DebugUtil;

/**
 * @author semancik
 *
 */
public class ItemPath implements Serializable {
	
	public static final ItemPath EMPTY_PATH = new ItemPath();
	
	private List<ItemPathSegment> segments;

	public ItemPath() {
		segments = new ArrayList<ItemPathSegment>(0);
	}
	
//	public PropertyPath(List<QName> qnames) {
//		this.segments = new ArrayList<PropertyPathSegment>(qnames.size());
//		addAll(qnames);
//	}
			
//	public PropertyPath(List<QName> qnames, QName subName) {
//		this.segments = new ArrayList<PropertyPathSegment>(qnames.size()+1);
//		addAll(qnames);
//		add(subName);
//	}
	
	public ItemPath(QName... qnames) {
		this.segments = new ArrayList<ItemPathSegment>(qnames.length);
		for (QName qname : qnames) {
			add(qname);
		}
	}
	
	public ItemPath(ItemPath parentPath, QName subName) {
		this.segments = new ArrayList<ItemPathSegment>(parentPath.segments.size()+1);
		segments.addAll(parentPath.segments);
		add(subName);
	}

	
	public ItemPath(List<ItemPathSegment> segments) {
		this.segments = new ArrayList<ItemPathSegment>(segments.size());
		this.segments.addAll(segments);
	}
			
	public ItemPath(List<ItemPathSegment> segments, ItemPathSegment subSegment) {
		this.segments = new ArrayList<ItemPathSegment>(segments.size()+1);
		this.segments.addAll(segments);
		this.segments.add(subSegment);
	}
	
	public ItemPath(List<ItemPathSegment> segments, QName subName) {
		this.segments = new ArrayList<ItemPathSegment>(segments.size()+1);
		this.segments.addAll(segments);
		add(subName);
	}
	
	public ItemPath(ItemPathSegment... segments) {
		this.segments = new ArrayList<ItemPathSegment>(segments.length);
		for (ItemPathSegment seg : segments) {
			this.segments.add(seg);
		}
	}
	
	public ItemPath(ItemPath parentPath, ItemPathSegment subSegment) {
		this.segments = new ArrayList<ItemPathSegment>(parentPath.segments.size()+1);
		this.segments.addAll(parentPath.segments);
		this.segments.add(subSegment);
	}

	public ItemPath subPath(QName subName) {
		return new ItemPath(segments, subName);
	}
	
	public ItemPath subPath(ItemPathSegment subSegment) {
		return new ItemPath(segments, subSegment);
	}

	public ItemPath subPath(ItemPath subPath) {
		ItemPath newPath = new ItemPath(segments);
		newPath.segments.addAll(subPath.getSegments());
		return newPath;
	}

//	private void addAll(List<QName> qnames) {
//		for (QName qname: qnames) {
//			add(qname);
//		}
//	}
	
	private void add(QName qname) {
		this.segments.add(new ItemPathSegment(qname));
	}
		
	public List<ItemPathSegment> getSegments() {
		return segments;
	}
	
	public ItemPathSegment first() {
		if (segments.size() == 0) {
			return null;
		}
		return segments.get(0);
	}

	public ItemPath rest() {
		if (segments.size() == 0) {
			return EMPTY_PATH;
		}
		return new ItemPath(segments.subList(1, segments.size()));
	}
	
	public ItemPathSegment last() {
		if (segments.size() == 0) {
			return null;
		}
		return segments.get(segments.size()-1);
	}

	/**
	 * Returns first segment in a form of path.
	 */
	public ItemPath head() {
		return new ItemPath(first());
	}
	
	/**
	 * Returns path containinig all segments except the first.
	 */
	public ItemPath tail() {
		if (segments.size() == 0) {
			return EMPTY_PATH;
		}
		return new ItemPath(segments.subList(1, segments.size()));
	}

	/**
	 * Returns a path containing all segments except the last one.
	 */
	public ItemPath allExceptLast() {
		if (segments.size() == 0) {
			return EMPTY_PATH;
		}
		return new ItemPath(segments.subList(0, segments.size()-1));
	}
	
	public int size() {
		return segments.size();
	}

	public boolean isEmpty() {
		return segments.isEmpty();
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		Iterator<ItemPathSegment> iterator = segments.iterator();
		while (iterator.hasNext()) {
			sb.append(iterator.next());
			if (iterator.hasNext()) {
				sb.append("/");
			}
		}
		return sb.toString();
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((segments == null) ? 0 : segments.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		ItemPath other = (ItemPath) obj;
		if (segments == null) {
			if (other.segments != null)
				return false;
		} else if (!segments.equals(other.segments))
			return false;
		return true;
	}
	
}