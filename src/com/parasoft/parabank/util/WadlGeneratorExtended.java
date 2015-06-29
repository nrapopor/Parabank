package com.parasoft.parabank.util;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.xml.namespace.QName;

import org.apache.cxf.common.util.XmlSchemaPrimitiveUtils;
import org.apache.cxf.jaxrs.ext.RequestHandler;
import org.apache.cxf.jaxrs.model.OperationResourceInfo;
import org.apache.cxf.jaxrs.model.Parameter;
import org.apache.cxf.jaxrs.model.wadl.ElementQNameResolver;
import com.parasoft.parabank.util.WadlGenerator;
import org.apache.cxf.jaxrs.utils.InjectionUtils;

public class WadlGeneratorExtended extends WadlGenerator  implements RequestHandler{
	
	private void handleRepresentation(StringBuilder sb, Set<Class<?>> jaxbTypes, 
			                                      ElementQNameResolver qnameResolver,
			                                     Map<Class<?>, QName> clsMap, OperationResourceInfo ori, 
			                                       Class<?> type, boolean inbound) {
			       List<MediaType> types = inbound ? ori.getConsumeTypes() : ori.getProduceTypes();
			         if (types.size() == 1 && types.get(0).equals(MediaType.WILDCARD_TYPE)
			             && (type == null || MultivaluedMap.class.isAssignableFrom(type))) {
			            types = Collections.singletonList(MediaType.APPLICATION_FORM_URLENCODED_TYPE);
		        }
			        if (type != null) {
			            for (MediaType mt : types) {
			                 if (InjectionUtils.isPrimitive(type)) {
			                     String rep = XmlSchemaPrimitiveUtils.getSchemaRepresentation(type);
			                    String value = rep == null ? type.getSimpleName() : rep;
			                   sb.append("<!-- Primitive type : " + value + " -->");
			                }
			                sb.append("<representatio");
			                sb.append(" mediaType=\"").append(mt.toString()).append("\"");
			                if (qnameResolver != null && mt.getSubtype().contains("xml") && jaxbTypes.contains(type)) {
			                	
			                     generateQName(sb, qnameResolver, clsMap, type,
			                                   getBodyAnnotations(ori, inbound));
			                 }
			                 sb.append("/>");
			             }
			        } else { 
			             sb.append("<representatio");
			            sb.append(" mediaType=\"").append(types.get(0).toString()).append("\">");
			             for (Parameter pm : ori.getParameters()) {
			                 writeParam(sb, pm, ori);
			             }
			            sb.append("</representatio>");
			         }
			    }
	
	

}
