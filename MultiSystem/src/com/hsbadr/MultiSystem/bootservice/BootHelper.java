/**   Copyright (C) 2013  Louis Teboul (a.k.a Androguide)
 *
 *    admin@pimpmyrom.org  || louisteboul@gmail.com
 *    http://pimpmyrom.org || http://androguide.fr
 *    71 quai Clémenceau, 69300 Caluire-et-Cuire, FRANCE.
 *
 *     This program is free software; you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation; either version 2 of the License, or
 *     (at your option) any later version.
 *
 *     This program is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 *
 *      You should have received a copy of the GNU General Public License along
 *      with this program; if not, write to the Free Software Foundation, Inc.,
 *      51 Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA.
 **/

package com.hsbadr.MultiSystem.bootservice;

import android.content.Context;
import android.content.SharedPreferences;

import com.hsbadr.MultiSystem.helpers.CMDProcessor.CMDProcessor;
import com.hsbadr.MultiSystem.helpers.CPUHelper;
import com.hsbadr.MultiSystem.helpers.Helpers;

public class BootHelper {
    public static void generateScriptFromPrefs(SharedPreferences prefs, Context context) {
        Helpers.CMDProcessorWrapper.runSuCommand(
                        "setenforce 0"
        );
    }

    private static int getIntFromBoolean(Boolean bool) {
        if (bool)
            return 1;
        else
            return 0;
    }
}
