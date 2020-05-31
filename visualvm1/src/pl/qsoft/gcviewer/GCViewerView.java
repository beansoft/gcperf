/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package pl.qsoft.gcviewer;

import com.sun.tools.visualvm.application.Application;
import com.sun.tools.visualvm.charts.ChartFactory;
import com.sun.tools.visualvm.charts.SimpleXYChartDescriptor;
import com.sun.tools.visualvm.charts.SimpleXYChartSupport;
import com.sun.tools.visualvm.core.ui.DataSourceView;
import com.sun.tools.visualvm.core.ui.components.DataViewComponent;
import com.sun.tools.visualvm.tools.jvmstat.JvmstatModel;
import com.sun.tools.visualvm.tools.jvmstat.JvmstatModelFactory;
import java.util.HashMap;
import java.util.Map;
import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JEditorPane;
import javax.swing.JPanel;
import org.openide.util.Utilities;

class GCViewerView extends DataSourceView {

    private static final String IMAGE_PATH = "com/sun/tools/visualvm/coredump/resources/coredump.png"; // NOI18N
    private final Application application;
    private final Map<SimpleXYChartSupport, String[]> charts = new HashMap<SimpleXYChartSupport, String[]>();
    private DataViewComponent dvc;

    public GCViewerView(Application application) {
        super(application, java.util.ResourceBundle.getBundle("pl/qsoft/gcviewer/Bundle").getString("GC_VIEWER"), new ImageIcon(Utilities.loadImage(IMAGE_PATH, true)).getImage(), 60, false);
        this.application = application;
    }

    private SimpleXYChartSupport createChart(DataTypeEnum dataType,
            ChartTypeEnum chartType, String name, String[] lineItems,
            String xDesc, String yDesc, String[] dataItemKeys) {

        SimpleXYChartDescriptor description;
        if (dataType == DataTypeEnum.DECIMAL) {
            description = SimpleXYChartDescriptor.decimal(0, true, 1000);
        } else {
            description = SimpleXYChartDescriptor.bytes(0, true, 1000);
        }

        description.setChartTitle(name);

        if (chartType == ChartTypeEnum.LINE) {
            description.addLineItems(lineItems);
        } else {
            description.addFillItems(lineItems);
        }

        description.setXAxisDescription(xDesc);
        description.setYAxisDescription(yDesc);
        final SimpleXYChartSupport chart = ChartFactory.createSimpleXYChart(description);
        charts.put(chart, dataItemKeys);
        return chart;
    }

    @Override
    protected DataViewComponent createComponent() {
        JEditorPane generalDataArea = new JEditorPane();
        generalDataArea.setBorder(BorderFactory.createEmptyBorder(14, 8, 14, 8));
        JPanel panel = new JPanel();

        DataViewComponent.MasterView masterView = new DataViewComponent.MasterView(java.util.ResourceBundle.getBundle("pl/qsoft/gcviewer/Bundle").getString("GC PERFORMANCE OVERVIEW"), null, generalDataArea);
        DataViewComponent.MasterViewConfiguration masterConfiguration =
                new DataViewComponent.MasterViewConfiguration(false);

        dvc = new DataViewComponent(masterView, masterConfiguration);

        SimpleXYChartSupport pauseChart = createChart(DataTypeEnum.DECIMAL, ChartTypeEnum.LINE, java.util.ResourceBundle.getBundle("pl/qsoft/gcviewer/Bundle").getString("GC PAUSE TIMES"),
                new String[]{java.util.ResourceBundle.getBundle("pl/qsoft/gcviewer/Bundle").getString("MINOR GC PAUSE"), java.util.ResourceBundle.getBundle("pl/qsoft/gcviewer/Bundle").getString("MAJOR GC PAUSE")}, java.util.ResourceBundle.getBundle("pl/qsoft/gcviewer/Bundle").getString("<HTML>TIME</HTML>"), java.util.ResourceBundle.getBundle("pl/qsoft/gcviewer/Bundle").getString("<HTML>GC  PAUSE  IN  ΜS</HTML>"),
                new String[]{GcDataCollector.MINOR_GC_TIME_KEY, GcDataCollector.MAJOR_GC_TIME_KEY});
        dvc.addDetailsView(new DataViewComponent.DetailsView(java.util.ResourceBundle.getBundle("pl/qsoft/gcviewer/Bundle").getString("GC PAUSE TIMES"), "description", 0, pauseChart.getChart(), null), DataViewComponent.TOP_LEFT);

        SimpleXYChartSupport survivorChart = createChart(DataTypeEnum.BYTES, ChartTypeEnum.LINE, java.util.ResourceBundle.getBundle("pl/qsoft/gcviewer/Bundle").getString("PROMOTED VS SURVIVED"),
                new String[]{java.util.ResourceBundle.getBundle("pl/qsoft/gcviewer/Bundle").getString("PROMOTED"), java.util.ResourceBundle.getBundle("pl/qsoft/gcviewer/Bundle").getString("SURVIVED")}, java.util.ResourceBundle.getBundle("pl/qsoft/gcviewer/Bundle").getString("<HTML>TIME</HTML>"), java.util.ResourceBundle.getBundle("pl/qsoft/gcviewer/Bundle").getString("<HTML>BYTES</HTML>"),
                new String[]{GcDataCollector.PROMOTED_KEY, GcDataCollector.SURVIVED_KEY});
        dvc.addDetailsView(new DataViewComponent.DetailsView(java.util.ResourceBundle.getBundle("pl/qsoft/gcviewer/Bundle").getString("PROMOTED VS SURVIVED"), "description", 0, survivorChart.getChart(), null), DataViewComponent.TOP_RIGHT);

        SimpleXYChartSupport gcCostChart = createChart(DataTypeEnum.DECIMAL, ChartTypeEnum.LINE, java.util.ResourceBundle.getBundle("pl/qsoft/gcviewer/Bundle").getString("GC COST"),
                new String[]{java.util.ResourceBundle.getBundle("pl/qsoft/gcviewer/Bundle").getString("MINOR GC COST"), java.util.ResourceBundle.getBundle("pl/qsoft/gcviewer/Bundle").getString("MAJOR GC COST")}, java.util.ResourceBundle.getBundle("pl/qsoft/gcviewer/Bundle").getString("<HTML>TIME</HTML>"), java.util.ResourceBundle.getBundle("pl/qsoft/gcviewer/Bundle").getString("<HTML>COST</HTML>"),
                new String[]{GcDataCollector.MINOR_COST_KEY, GcDataCollector.MAJOR_COST_KEY});
        dvc.addDetailsView(new DataViewComponent.DetailsView(java.util.ResourceBundle.getBundle("pl/qsoft/gcviewer/Bundle").getString("GC COST"), "description", 0, gcCostChart.getChart(), null), DataViewComponent.BOTTOM_LEFT);

        SimpleXYChartSupport freeLiveChart = createChart(DataTypeEnum.BYTES, ChartTypeEnum.LINE, java.util.ResourceBundle.getBundle("pl/qsoft/gcviewer/Bundle").getString("FREE & LIVE SPACE"),
                new String[]{java.util.ResourceBundle.getBundle("pl/qsoft/gcviewer/Bundle").getString("FREE SPACE"), java.util.ResourceBundle.getBundle("pl/qsoft/gcviewer/Bundle").getString("LIVE SPACE")}, java.util.ResourceBundle.getBundle("pl/qsoft/gcviewer/Bundle").getString("<HTML>TIME</HTML>"), java.util.ResourceBundle.getBundle("pl/qsoft/gcviewer/Bundle").getString("<HTML>BYTES</HTML>"),
                new String[]{GcDataCollector.FREE_SPACE_KEY, GcDataCollector.LIVE_SPACE_KEY});
        dvc.addDetailsView(new DataViewComponent.DetailsView(java.util.ResourceBundle.getBundle("pl/qsoft/gcviewer/Bundle").getString("FREE & LIVE SPACE"), "description", 0, freeLiveChart.getChart(), null), DataViewComponent.BOTTOM_RIGHT);

        final JvmstatModel model = JvmstatModelFactory.getJvmstatFor(application);
        final GcPauseMonitor monitor = new GcPauseMonitor(new GcDataCollector(model, charts));
        new Thread(monitor).start();

        return dvc;
    }
}
