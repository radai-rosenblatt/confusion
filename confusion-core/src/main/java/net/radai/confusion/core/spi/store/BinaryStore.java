/*
 * This file is part of Confusion.
 *
 * Confusion is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Confusion is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with Confusion.  If not, see <http://www.gnu.org/licenses/>.
 */

package net.radai.confusion.core.spi.store;

import net.radai.confusion.core.spi.PayloadType;

import java.io.IOException;

/**
 * Created by Radai Rosenblatt
 */
public interface BinaryStore extends Store {

    @Override
    default PayloadType getPayloadType() {
        return PayloadType.BINARY;
    }

    byte[] read() throws IOException;
    void write(byte[] payload) throws IOException;
    void register(BinaryStoreListener newListener);
    void unregister(BinaryStoreListener existingListener);
}
