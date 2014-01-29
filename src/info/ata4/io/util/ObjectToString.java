/*
 ** 2013 November 25
 **
 ** The author disclaims copyright to this source code.  In place of
 ** a legal notice, here is a blessing:
 **    May you do good and not evil.
 **    May you find forgiveness for yourself and forgive others.
 **    May you share freely, never taking more than you give.
 */
package info.ata4.io.util;

import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import org.apache.commons.lang3.SystemUtils;
import org.apache.commons.lang3.builder.ReflectionToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

/**
 *
 * @author Nico Bergemann <barracuda415 at yahoo.de>
 */
public class ObjectToString {

    private static final ToStringStyle STYLE = new ObjectToStringStyle();
    
    public static String toString(Object obj) {
        return ReflectionToStringBuilder.toString(obj, STYLE);
    }
    
    private static class ObjectToStringStyle extends ToStringStyle {
        
        private static final String INDENT = "  ";
        
        private StringBuffer indentBuffer = new StringBuffer();

        ObjectToStringStyle() {
            super();
            setUseIdentityHashCode(false);
            setUseShortClassName(true);
            setArrayStart("[");
            setArrayEnd("]");
            setContentStart(" {");
            setFieldSeparatorAtStart(true);
            setFieldNameValueSeparator(" = ");

            updateSeparators();
        }

        private void indent() {
            indentBuffer.append(INDENT);
            updateSeparators();
        }

        private void unindent() {
            int len = indentBuffer.length();
            if (indentBuffer.length() > 0) {
                indentBuffer.setLength(len - INDENT.length());
            }
            updateSeparators();
        }

        @Override
        protected void appendDetail(StringBuffer buffer, String fieldName, Object value) {
            appendObject(buffer, value);
        }
        
        @Override
        protected void appendDetail(StringBuffer buffer, String fieldName, Collection<?> coll) {
            indent();
            buffer.append(getArrayStart());
            buffer.append(getFieldSeparator());
            
            Iterator<?> it = coll.iterator();
            while (it.hasNext()) {
                appendObject(buffer, it.next());
                
                if (it.hasNext()) {
                    buffer.append(getArraySeparator());
                    buffer.append(getFieldSeparator());
                }
            }

            unindent();
            buffer.append(getFieldSeparator());
            buffer.append(getArrayEnd());
        }
        
        @Override
        protected void appendDetail(StringBuffer buffer, String fieldName, Map<?, ?> map) {
            indent();
            buffer.append(getArrayStart());
            buffer.append(getFieldSeparator());
            
            Iterator<? extends Map.Entry> it = map.entrySet().iterator();
            
            while(it.hasNext()) {
                Map.Entry entry = it.next();
                
                appendObject(buffer, entry.getKey());
                buffer.append(getFieldNameValueSeparator());
                appendObject(buffer, entry.getValue());
                
                if (it.hasNext()) {
                    buffer.append(getArraySeparator());
                    buffer.append(getFieldSeparator());
                }
            }

            unindent();
            buffer.append(getFieldSeparator());
            buffer.append(getArrayEnd());
        }
        
        private void appendObject(StringBuffer buffer, Object obj) {
            if (hasDefaultToString(obj)) {
                indent();
                buffer.append(ReflectionToStringBuilder.toString(obj, this));
                unindent();
            } else {
                buffer.append(obj);
            }
        }
        
        private boolean hasDefaultToString(Object obj) {
            try {
                // check if the declaring class of toString() is java.lang.Object
                return obj.getClass().getMethod("toString").getDeclaringClass() == Object.class;
            } catch (SecurityException ex) {
                // class with security manager?
                return false;
            } catch (NoSuchMethodException ex) {
                // wat
                return true;
            }
        }

        private void updateSeparators() {
            String indentGlobal = indentBuffer.toString();
            setFieldSeparator(SystemUtils.LINE_SEPARATOR + indentGlobal + INDENT);
            setContentEnd(SystemUtils.LINE_SEPARATOR + indentGlobal + "}");
        }
    }

    private ObjectToString() {
    }
}
