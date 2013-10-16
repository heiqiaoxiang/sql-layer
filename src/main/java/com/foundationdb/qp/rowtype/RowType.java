/**
 * Copyright (C) 2009-2013 FoundationDB, LLC
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package com.foundationdb.qp.rowtype;

// Fields are untyped for now. Field name is just position within the type.

import com.foundationdb.ais.model.Column;
import com.foundationdb.ais.model.HKey;
import com.foundationdb.ais.model.UserTable;
import com.foundationdb.server.types.TInstance;
import com.foundationdb.server.explain.*;

public abstract class RowType
{
    // Object interface

    @Override
    public String toString()
    {
        return String.format("RowType(%s)", typeId);
    }

    @Override
    public int hashCode()
    {
        return typeId * 9987001;
    }

    @Override
    public boolean equals(Object o)
    {
        return o == this || o != null && o instanceof RowType && this.typeId == ((RowType)o).typeId;
    }

    // RowType interface

    public abstract DerivedTypesSchema schema();

    public final int typeId()
    {
        return typeId;
    }

    public final TypeComposition typeComposition()
    {
        return typeComposition;
    }

    public final boolean ancestorOf(RowType that)
    {
        return that.typeComposition != null && this.typeComposition.isAncestorOf(that.typeComposition);
    }

    public final boolean parentOf(RowType that)
    {
        return that.typeComposition != null && this.typeComposition.isParentOf(that.typeComposition);
    }

    public abstract int nFields();

    public abstract TInstance typeInstanceAt(int index);

    public ConstraintChecker constraintChecker()
    {
        return NOOP_CONSTRAINT_CHECKER;
    }

    public HKey hKey()
    {
        return null;
    }

    /**
     * <p>Gets the AIS Column for a given field. This method may throw an exception if the RowType doesn't have
     * an AIS column at the given index, for whatever reason. This may be because the question doesn't make sense
     * ({@code SELECT 1;}), or because the implementation doesn't track information that it technically could.</p>
     *
     * <p>Regardless, you can invoke {@linkplain #fieldHasColumn(int)} to determine if this method will throw
     * an exception. If that method returns {@code true}, this method may not throw an exception.</p>
     * @param field the field index for which you want a column
     * @return the Column that corresponds to that field
     * @throws FieldHasNoColumnException if the field doesn't correspond to any Column
     * @throws IndexOutOfBoundsException if {@code field >= nFields() || field < 0}
     */
    public Column fieldColumn(int field) {
        checkFieldRange(field);
        throw new FieldHasNoColumnException(field);
    }

    /**
     * <p>Returns whether the given field corresponds to a Column. If it does, {@linkplain #fieldColumn(int)} will
     * not throw an exception. If this method returns {@code false}, getting the field's column may (and probably will)
     * result in an exception.</p>
     * @param field the field for which you want a column
     * @return whether that field corresponds to a Column
     * @throws IndexOutOfBoundsException if {@code field >= nFields() || field < 0}
     */
    public boolean fieldHasColumn(int field) {
        checkFieldRange(field);
        return false;
    }

    /**
     * <p>Get the one user table that this row type corresponds to. Not all row types correspond to one user table;
     * a flattened row doesn't, nor does a row type that adds fields. For instance, the final row type for a query
     * {@code SELECT cid FROM customer} may (but does not have to) have a corresponding UserTable; the row type
     * for {@code SELECT 1, cid FROM customer} may not, since the first field doesn't correspond to any column
     * in the {@code customer} table.</p>
     *
     * <p>If this row type doesn't correspond to a user table, it will throw an exception. You can test for that
     * using {@linkplain #hasUserTable()}. If that method returns true, this method may not throw an exception.</p>
     *
     * <p>If this method doesn't throw an exception, several other things must be true:
     * <ul>
     *     <li>{@code fieldHasColumn(n) == true} for {@code 0 <= n < nFields()}</li>
     *     <li>{@code fieldColumn(n).getTable() == userTable()} (for same range of {@code n}</li>
     * </ul></p>
     * @return the user table associated with this row
     * @throws RowTypeToTableMappingException if there is no user table associated with this row
     */
    public UserTable userTable() {
        throw new RowTypeToTableMappingException("default RowType implementation has no UserTable");
    }

    public boolean hasUserTable() {
        return false;
    }
    
    // Will want to override in most cases.
    public CompoundExplainer getExplainer(ExplainContext context) {
        Attributes atts = new Attributes();
        atts.put(Label.NAME, PrimitiveExplainer.getInstance(toString()));
        return new CompoundExplainer(Type.ROWTYPE, atts);
    }

    // For use by subclasses

    protected void typeComposition(TypeComposition typeComposition)
    {
        this.typeComposition = typeComposition;
    }

    protected void checkFieldRange(int field) {
        if (field < 0) {
            throw new IndexOutOfBoundsException("field index must be >= 0: was " + field);
        }
        if (field >= nFields()) {
            throw new IndexOutOfBoundsException(String.format("field (%d) >= fields count (%d)", field, nFields()));
        }
    }

    protected RowType(int typeId)
    {
        this.typeId = typeId;
    }

    // Class state

    private static final ConstraintChecker NOOP_CONSTRAINT_CHECKER = new NoopConstraintChecker();

    // Object state

    private final int typeId;
    private TypeComposition typeComposition;
}