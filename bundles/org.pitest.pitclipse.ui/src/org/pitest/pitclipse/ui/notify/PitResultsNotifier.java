/*******************************************************************************
 * Copyright 2012-2019 Phil Glover and contributors
 *  
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License.  You may obtain a copy
 * of the License at
 *  
 *  http://www.apache.org/licenses/LICENSE-2.0
 *  
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  See the
 * License for the specific language governing permissions and limitations under
 * the License.
 ******************************************************************************/

package org.pitest.pitclipse.ui.notify;

import org.eclipse.swt.widgets.Display;
import org.pitest.pitclipse.core.extension.point.ResultNotifier;
import org.pitest.pitclipse.runner.PitResults;
import org.pitest.pitclipse.ui.view.PitViewFinder;
import org.pitest.pitclipse.ui.view.SummaryView;

/**
 * <p>Updates the <i>Summary View</i> with given PIT results.</p>
 * 
 * <p>This class is registered through the {@code results} extension point
 * and is hence called each time new results are produced by PIT.</p>
 */
public class PitResultsNotifier implements ResultNotifier<PitResults> {
    
    @Override
    public void handleResults(PitResults results) {
        Display.getDefault().asyncExec(() -> {
            SummaryView view = PitViewFinder.INSTANCE.getSummaryView();
            view.update(results.getHtmlResultFile());
        });
    }
    
}
