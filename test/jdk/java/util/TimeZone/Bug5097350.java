/*
 * Copyright (c) 2004, 2025, Oracle and/or its affiliates. All rights reserved.
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER.
 *
 * This code is free software; you can redistribute it and/or modify it
 * under the terms of the GNU General Public License version 2 only, as
 * published by the Free Software Foundation.
 *
 * This code is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License
 * version 2 for more details (a copy is included in the LICENSE file that
 * accompanied this code).
 *
 * You should have received a copy of the GNU General Public License version
 * 2 along with this work; if not, write to the Free Software Foundation,
 * Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 USA.
 *
 * Please contact Oracle, 500 Oracle Parkway, Redwood Shores, CA 94065 USA
 * or visit www.oracle.com if you need additional information or have any
 * questions.
 */

/*
 * @test
 * @bug 5097350 8347841 8347955
 * @summary Make sure that TimeZone.getTimeZone returns a clone of a cached TimeZone instance.
 */

import java.time.ZoneId;
import java.util.*;
import java.util.function.Predicate;

public class Bug5097350 {
    public static void main(String[] args) {
        String[] tzids = TimeZone.availableIDs()
                .filter(Predicate.not(ZoneId.SHORT_IDS::containsKey))
                .toArray(String[]::new);
        List<String> ids = new ArrayList<>(tzids.length + 10);
        ids.addAll(Arrays.asList(tzids));
        // add some custom ids
        ids.add("GMT+1");
        ids.add("GMT-7:00");
        ids.add("GMT+10:20");
        ids.add("GMT-00:00");
        ids.add("GMT+00:00");

        for (String id : ids) {
            test(id);
        }
    }

    private static void test(String id) {
        TimeZone tz1 = TimeZone.getTimeZone(id);
        int offset1 = tz1.getRawOffset();
        tz1.setRawOffset(offset1 + 13 * 60 * 60 * 1000);

        TimeZone tz2 = TimeZone.getTimeZone(id);
        if (tz1 == tz2) {
            throw new RuntimeException("TimeZones are identical: " + id);
        }
        if (offset1 != tz2.getRawOffset()) {
            throw new RuntimeException("Offset changed through aliasing: " + id);
        }
    }
}
