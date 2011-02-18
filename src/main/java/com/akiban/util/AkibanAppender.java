/**
 * Copyright (C) 2011 Akiban Technologies Inc.
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License, version 3,
 * as published by the Free Software Foundation.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see http://www.gnu.org/licenses.
 */

package com.akiban.util;

import java.io.PrintWriter;

public abstract class AkibanAppender {
    public abstract void append(Object o);
    public abstract void append(char c);
    public abstract void append(String s);
    public abstract Appendable getAppendable();

    public static AkibanAppender of(StringBuilder stringBuilder) {
        return new AkibanAppenderSB(stringBuilder);
    }

    public static AkibanAppender of(PrintWriter printWriter) {
        return new AkibanAppenderPW(printWriter);
    }

    private static class AkibanAppenderPW extends AkibanAppender
    {
        private final PrintWriter pr;

        public AkibanAppenderPW(PrintWriter pr) {
            this.pr = pr;
        }

        @Override
        public void append(Object o) {
            pr.append(o == null ? "null" : o.toString());
        }

        @Override
        public void append(char c) {
            pr.print(c);
        }

        @Override
        public void append(String s) {
            pr.print(s);
        }

        @Override
        public Appendable getAppendable() {
            return pr;
        }
    }

    private static class AkibanAppenderSB extends AkibanAppender
    {
        private final StringBuilder sb;

        public AkibanAppenderSB(StringBuilder sb) {
            this.sb = sb;
        }

        @Override
        public void append(Object o) {
            sb.append(o);
        }

        @Override
        public void append(char c) {
            sb.append(c);
        }

        @Override
        public void append(String s) {
            sb.append(s);
        }

        @Override
        public Appendable getAppendable() {
            return sb;
        }

        @Override
        public String toString() {
            return sb.toString();
        }
    }
}
