/*
 * ==========================================================================================
 * =                   JAHIA'S DUAL LICENSING - IMPORTANT INFORMATION                       =
 * ==========================================================================================
 *
 *                                 http://www.jahia.com
 *
 *     Copyright (C) 2002-2020 Jahia Solutions Group SA. All rights reserved.
 *
 *     THIS FILE IS AVAILABLE UNDER TWO DIFFERENT LICENSES:
 *     1/GPL OR 2/JSEL
 *
 *     1/ GPL
 *     ==================================================================================
 *
 *     IF YOU DECIDE TO CHOOSE THE GPL LICENSE, YOU MUST COMPLY WITH THE FOLLOWING TERMS:
 *
 *     This program is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *
 *     This program is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 *     GNU General Public License for more details.
 *
 *     You should have received a copy of the GNU General Public License
 *     along with this program. If not, see <http://www.gnu.org/licenses/>.
 *
 *
 *     2/ JSEL - Commercial and Supported Versions of the program
 *     ===================================================================================
 *
 *     IF YOU DECIDE TO CHOOSE THE JSEL LICENSE, YOU MUST COMPLY WITH THE FOLLOWING TERMS:
 *
 *     Alternatively, commercial and supported versions of the program - also known as
 *     Enterprise Distributions - must be used in accordance with the terms and conditions
 *     contained in a separate written agreement between you and Jahia Solutions Group SA.
 *
 *     If you are unsure which license is appropriate for your use,
 *     please contact the sales department at sales@jahia.com.
 */
package org.jahia.services.seo;

import org.apache.hc.core5.http.HttpException;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

import java.util.Arrays;
import java.util.Collection;

import static org.junit.Assert.assertEquals;

/**
 * Unit test for vanity URL creation (only processing of the URL string).
 *
 * @author Sergiy Shyrkov
 */
@RunWith(Parameterized.class)
public class VanityUrlTest {

    @Parameters
    public static Collection<Object[]> data() {
        return Arrays.asList(
                new Object[][] { { null, null }, { "", "" }, { "aaa", "/aaa" }, { "/aaa", "/aaa" }, { "/aaa/", "/aaa" },
                        { "/aaa////", "/aaa" }, { "//aaa", "/aaa" }, { "////aaa", "/aaa" }, { "/aaa/", "/aaa" },
                        { "/aaa//", "/aaa" }, { "//aaa/", "/aaa" }, { "//aaa//", "/aaa" }, { "////aaa////", "/aaa" } });
    }

    private String input;

    private String expectedResult;

    public VanityUrlTest(String input, String expectedResult) {
        this.input = input;
        this.expectedResult = expectedResult;
    }

    @Test
    public void testVanityUrl() throws HttpException {
        assertEquals("Expected URL value for input '" + input + "' should be '" + expectedResult + "'", expectedResult,
                new VanityUrl(input, null, null).getUrl());
    }
}
