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

package com.foundationdb.server.types.mcompat.mfuncs;

import com.foundationdb.server.types.TScalar;
import com.foundationdb.server.types.common.funcs.TAesEncryptDecrypt;
import com.foundationdb.server.types.mcompat.mtypes.MBinary;
import com.foundationdb.server.types.mcompat.mtypes.MString;

public class MEncryption
{
    // MySQL's default key length for aes_encrypt/decrypt
    public static final int DEFAULT_KEY_LENGTH = 16;
    
    public static final TScalar[] AES_CRYPTOS
            = TAesEncryptDecrypt.create(MString.VARCHAR, MBinary.VARBINARY, DEFAULT_KEY_LENGTH);
}
