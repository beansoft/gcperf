/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package pl.qsoft.gcviewer;

import org.graalvm.visualvm.application.Application;
import org.graalvm.visualvm.core.ui.DataSourceView;
import org.graalvm.visualvm.core.ui.DataSourceViewProvider;
import org.graalvm.visualvm.core.ui.DataSourceViewsManager;

/**
 *
 * @author qdlt
 */
class GCViewerProvider extends DataSourceViewProvider<Application> {

private static DataSourceViewProvider<Application> instance =  new GCViewerProvider();

    @Override
    public boolean supportsViewFor(Application application) {
        //Always shown:
        return true;
    }

    @Override
    public synchronized DataSourceView createView(final Application application) {
        return new GCViewerView(application);

    }

    static void initialize() {
        DataSourceViewsManager.sharedInstance().addViewProvider(instance, Application.class);
    }

    static void unregister() {
        DataSourceViewsManager.sharedInstance().removeViewProvider(instance);
    }
}
