/*
 * This file is part of ConfigLib.
 *
 * ConfigLib is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * ConfigLib is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with ConfigLib.  If not, see <http://www.gnu.org/licenses/>.
 */

package net.radai.configlib.core.runtime;

import java.lang.reflect.Field;
import java.lang.reflect.Type;

/**
 * Created by Radai Rosenblatt
 */
public class FieldProperty implements Property {
    private final String name;
    private final Field field;

    public FieldProperty(String name, Field field) {
        this.name = name;
        this.field = field;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public Type getType() {
        return field.getGenericType();
    }

    @Override
    public boolean isReadable() {
        return true;
    }

    @Override
    public boolean isWritable() {
        return true;
    }
}
