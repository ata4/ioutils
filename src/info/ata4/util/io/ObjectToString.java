/*
 ** 2013 November 25
 **
 ** The author disclaims copyright to this source code.  In place of
 ** a legal notice, here is a blessing:
 **    May you do good and not evil.
 **    May you find forgiveness for yourself and forgive others.
 **    May you share freely, never taking more than you give.
 */
package info.ata4.util.io;

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
            String defaultToString = value.getClass().getName() + "@" + Integer.toHexString(value.hashCode());
            if (value.toString().equals(defaultToString)) {
                indent();
                buffer.append(ReflectionToStringBuilder.toString(value, this));
                unindent();
            } else {
                super.appendDetail(buffer, fieldName, value);
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
