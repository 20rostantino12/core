package org.jboss.as.console.client.shared.runtime.charts;

/**
 * @author Heiko Braun
 * @date 11/3/11
 */
public class NumberColumn extends Column<Integer> {

    public NumberColumn(String detypedName, String label) {
        super(ColumnType.NUMBER, label);
        setDeytpedName(detypedName);
    }

    @Override
    Integer cast(String value) {
        return Integer.valueOf(value).intValue();
    }
}
