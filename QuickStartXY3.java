import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.Cursor;
import java.awt.image.BufferedImage;
import java.io.*;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.*;
import java.awt.*;
import java.awt.event.*;
import com.esri.mo2.ui.bean.*;
import com.esri.mo2.ui.bean.Map;
import com.esri.mo2.ui.tb.ZoomPanToolBar;
import com.esri.mo2.ui.tb.SelectionToolBar;
import com.esri.mo2.ui.ren.LayerProperties;
import javax.swing.table.AbstractTableModel;
import com.esri.mo2.ui.dlg.AboutBox;
import javax.swing.table.TableColumn;
import com.esri.mo2.data.feat.*;
import com.esri.mo2.map.dpy.FeatureLayer;
import com.esri.mo2.map.dpy.BaseFeatureLayer;
import com.esri.mo2.map.draw.SimpleMarkerSymbol;
import com.esri.mo2.map.draw.BaseSimpleRenderer;
import com.esri.mo2.file.shp.*;
import com.esri.mo2.map.dpy.Layerset;
import com.esri.mo2.ui.bean.Tool;
import java.awt.geom.*;
import com.esri.mo2.cs.geom.*;
import java.io.IOException;
import com.esri.mo2.cs.geom.Envelope;
import com.esri.mo2.map.draw.*;
import java.net.URL;

class QuickStartXY3 extends JFrame {
    static Map map = new Map();
    static boolean fullMap = true;  // Map not zoomed
    static boolean helpToolOn;
    ResourceBundle names;                        //     (1)
    Locale loc1 = new Locale("es","MX");
    Locale loc2 = new Locale("en","US");
    Legend legend;
    Legend legend2;
    Layer layer = new Layer();
    Layer layer2 = new Layer();
    Layer layer3 = null;
    static AcetateLayer acetLayer;
    static com.esri.mo2.map.dpy.Layer layer4;
    com.esri.mo2.map.dpy.Layer activeLayer;
    int activeLayerIndex;
    com.esri.mo2.cs.geom.Point initPoint,endPoint;
    double distance;
    JButton englishjb = new JButton("English");
    JButton spanishjb = new JButton("Espa\u00F1ol");
    JMenuBar mbar = new JMenuBar();
    JMenu file = new JMenu("File");
    JMenu theme = new JMenu("Theme");
    JMenu layercontrol = new JMenu("LayerControl");
    JMenu help = new JMenu("Help");
    JMenuItem attribitem = new JMenuItem("Open Attribute Table",
            new ImageIcon("Icons/tableview.gif"));
    JMenuItem createlayeritem  = new JMenuItem("Create Layer from Selection",
            new ImageIcon("Icons/Icon0915b.jpg"));
    static JMenuItem promoteitem = new JMenuItem("Promote Selected Layer",
            new ImageIcon("Icons/promote1.GIF"));
    static JMenuItem promoteitemtotop = new JMenuItem("Promote to Top",
            new ImageIcon("Icons/promote.png"));
    JMenuItem demoteitem = new JMenuItem("Demote Selected Layer",
            new ImageIcon("Icons/demote.jpg"));
    JMenuItem printitem = new JMenuItem("Print",new ImageIcon("Icons/print.gif"));
    JMenuItem addlyritem = new JMenuItem("Add Layer",new ImageIcon("Icons/addtheme.gif"));
    JMenuItem remlyritem = new JMenuItem("Remove Layer",new ImageIcon("Icons/delete.gif"));
    JMenuItem propsitem = new JMenuItem("Legend Editor",new ImageIcon("Icons/properties.gif"));
    JMenuItem tocitem = new JMenuItem("Table of Contents",new ImageIcon("properties.gif"));
    JMenuItem legenditem = new JMenuItem("Legend Editor",new ImageIcon("properties.gif"));
    JMenuItem layercontrolitem = new JMenuItem("Layer Control",new ImageIcon("properties.gif"));
    JMenuItem contactitem = new JMenuItem("Contact us");
    JMenuItem aboutitem = new JMenuItem("About MOJO...");
    JMenuItem projectitem = new JMenuItem("About Project...");
    JMenu helptopics = new JMenu("Help Topics");
    JMenuItem helptoolitem = new JMenuItem("Help Tool",new ImageIcon("properties.gif"));
    URL urlhelp2 = getClass().getResource("Icons/help2.gif");
    URL urlhelp1 = getClass().getResource("Icons/help2.gif");
    Toc toc = new Toc();
    String s1 = "C:\\ESRI\\MOJ20\\Samples\\Data\\USA\\states.shp";
    //String s2 = "C:\\ESRI\\MOJ20\\Samples\\Data\\USA\\GLocs.shp";
String s2;
    String datapathname = "";
    String legendname = "";
    ZoomPanToolBar zptb = new ZoomPanToolBar();
    static SelectionToolBar stb = new SelectionToolBar();
    JToolBar jtb = new JToolBar();
    ComponentListener complistener;
    JLabel statusLabel = new JLabel("status bar    LOC");
    static JLabel milesLabel = new JLabel("   DIST:  0 mi    ");
    static JLabel kmLabel = new JLabel("  0 km    ");
    java.text.DecimalFormat df = new java.text.DecimalFormat("0.000");
    JPanel myjp = new JPanel();
    JPanel myjp2 = new JPanel();
    JButton prtjb = new JButton(new ImageIcon("Icons/print.gif"));
    JButton addlyrjb = new JButton(new ImageIcon("Icons/addtheme.gif"));
    JButton ptrjb = new JButton(new ImageIcon("Icons/pointer.gif"));
    JButton distjb = new JButton(new ImageIcon("Icons/measure_1.gif"));
    JButton hotjb = new JButton(new ImageIcon("Icons/hotlink.gif"));
    JButton XYjb = new JButton("XY");
    JButton helpjb = new JButton(new ImageIcon("Icons/properties.gif"));
    static HelpTool helpTool = new HelpTool();
    //Arrow arrow = new Arrow();
    //DistanceTool distanceTool= new DistanceTool();
    ActionListener lis;
    ActionListener layerlis;
    ActionListener layercontrollis;
    TocAdapter mytocadapter;
    ActionListener helplis;
    Toolkit tk = Toolkit.getDefaultToolkit();
    Image bolt = tk.getImage("Icons/hotlink_32x32-32.gif");  // 16x16 gif file
    Image helper = tk.getImage("Icons/properties.gif");
    java.awt.Cursor boltCursor = tk.createCustomCursor(bolt,new java.awt.Point(11,26),"bolt");
    java.awt.Cursor helpCursor = tk.createCustomCursor(helper, new java.awt.Point(2,2),"helper");
    MyPickAdapter picklis = new MyPickAdapter();
    Identify hotlink = new Identify(); //the Identify class implements a PickListener,
    static String mystate = null;
    static String stat = null;
    class MyPickAdapter implements PickListener {   //implements hotlink
        public void beginPick(PickEvent pe){};
        // this fires even when you click outside the states layer
        public void endPick(PickEvent pe){}
        public void foundData(PickEvent pe){

            FeatureLayer flayer2 = (FeatureLayer) pe.getLayer();
            com.esri.mo2.data.feat.Cursor c = pe.getCursor();
            Feature f = null;
            System.out.println("Inside foundData");
            Fields fields = null;
            if (c != null)
                f = (Feature)c.next();
            fields = f.getFields();
            String sname = fields.getField(4).getName(); //gets col. name for state name
            mystate = (String)f.getValue(4);
            String sname1 = fields.getField(5).getName(); //gets col. name for state name
            stat = (String)f.getValue(5);
            try {
                HotPick hotpick = new HotPick();//opens dialog window with image in it
                hotpick.setVisible(true);
            } catch(Exception e){}
        }
    };

    static Envelope env;
    public QuickStartXY3() {

        super("Quick Start");
        this.setBounds(100,100,700,450);
        zptb.setMap(map);
        stb.setMap(map);
        setJMenuBar(mbar);
        ActionListener lisZoom = new ActionListener() {
            public void actionPerformed(ActionEvent ae){
                fullMap = false;}}; // can change a boolean here
        ActionListener lisFullExt = new ActionListener() {
            public void actionPerformed(ActionEvent ae){
                fullMap = true;}};
        MouseAdapter mlLisZoom = new MouseAdapter() {
            public void mousePressed(MouseEvent me) {
                if (SwingUtilities.isRightMouseButton(me) && helpToolOn) {
                    try {
                        HelpDialog helpdialog = new HelpDialog((String)helpText.get(4));
                        helpdialog.setVisible(true);
                    } catch(IOException e){}
                }
            }
        };
        MouseAdapter mlLisZoomActive = new MouseAdapter() {
            public void mousePressed(MouseEvent me) {
                if (SwingUtilities.isRightMouseButton(me) && helpToolOn) {
                    try {
                        HelpDialog helpdialog = new HelpDialog((String)helpText.get(5));
                        helpdialog.setVisible(true);
                    } catch(IOException e){}
                }
            }
        };
        MouseAdapter mlLisDist = new MouseAdapter() {
            public void mousePressed(MouseEvent me) {
                if (SwingUtilities.isRightMouseButton(me) && helpToolOn) {
                    try {
                        HelpDialog helpdialog = new HelpDialog((String)helpText.get(6));
                        helpdialog.setVisible(true);
                    } catch(IOException e){}
                }
            }
        };
        MouseAdapter mlLisHotlink = new MouseAdapter() {
            public void mousePressed(MouseEvent me) {
                if (SwingUtilities.isRightMouseButton(me) && helpToolOn) {
                    try {
                        HelpDialog helpdialog = new HelpDialog((String)helpText.get(7));
                        helpdialog.setVisible(true);
                    } catch(IOException e){}
                }
            }
        };
        // next line gets ahold of a reference to the zoomin button
        JButton zoomInButton = (JButton)zptb.getActionComponent("ZoomIn");
        JButton zoomFullExtentButton =
                (JButton)zptb.getActionComponent("ZoomToFullExtent");
        JButton zoomToSelectedLayerButton =
                (JButton)zptb.getActionComponent("ZoomToSelectedLayer");
        zoomInButton.addActionListener(lisZoom);
        zoomFullExtentButton.addActionListener(lisFullExt);
        zoomInButton.addMouseListener(mlLisZoom);
        zoomToSelectedLayerButton.addMouseListener(mlLisZoomActive);
        distjb.addMouseListener(mlLisDist);
        hotjb.addMouseListener(mlLisHotlink);
        zoomToSelectedLayerButton.addActionListener(lisZoom);
        complistener = new ComponentAdapter () {
            public void componentResized(ComponentEvent ce) {
                if(fullMap) {
                    map.setExtent(env);
                    map.zoom(1.0);    //scale is scale factor in pixels
                    map.redraw();
                }
            }
        };

        addComponentListener(complistener);
        lis = new ActionListener() {public void actionPerformed(ActionEvent ae){
            Object source = ae.getSource();
            if (source == englishjb) {
                names = ResourceBundle.getBundle("NamesBundle1",loc2);  //   (2)
                java.util.List list = toc.getAllLegends();
                int count = list.size();
                System.out.println("CNT  "+count);
                for (int j =0;j<count;j++) {              //remove old layers
                    com.esri.mo2.map.dpy.Layer dpylayer1 =
                            (com.esri.mo2.map.dpy.Layer) ((Legend)list.get(j)).getLayer();
                    map.getLayerset().removeLayer(dpylayer1);
                }
                addShapefileToMap(layer,s1);
                addShapefileToMap(layer2,s2);translate();
            }
            else if (source == spanishjb) {
                System.out.println("span:  "+source);
                System.out.println("loc: "+loc1);
                System.out.println(ResourceBundle.getBundle("NamesBundle",loc1));
                names = ResourceBundle.getBundle("NamesBundle",loc1);
                java.util.List list = toc.getAllLegends();
                System.out.println("list:  "+list);
                int count = list.size();
                for (int j =0;j<count;j++) {              //remove old layers
                    com.esri.mo2.map.dpy.Layer  dpylayer1 =
                            (com.esri.mo2.map.dpy.Layer) ((Legend)list.get(j)).getLayer();
                    map.getLayerset().removeLayer(dpylayer1);
                }
                addShapefileToMap(layer,s1);
                addShapefileToMap(layer2,s2);translate();
            }
            else if (source == prtjb || source instanceof JMenuItem ) {
                com.esri.mo2.ui.bean.Print mapPrint = new com.esri.mo2.ui.bean.Print();
                mapPrint.setMap(map);
                mapPrint.doPrint();// prints the map
            }
            else if (source == ptrjb) {
                Arrow arrow = new Arrow();
                map.setSelectedTool(arrow);
            }
            else if (source == distjb) {
                DistanceTool distanceTool = new DistanceTool();
                map.setSelectedTool(distanceTool);
            }
            else if (source == hotjb) {
                hotlink.setCursor(boltCursor);
                map.setSelectedTool(hotlink);
            }
            else if (source == helpjb) {
                helpToolOn = true;
                helpTool.setCursor(helpCursor);
                map.setSelectedTool(helpTool);
            }
            else if (source == XYjb) {
                try {
                    AddXYtheme addXYtheme = new AddXYtheme();
                    addXYtheme.setMap(map);
                    addXYtheme.setVisible(false);// the file chooser needs a parent
                    // but the parent can stay behind the scenes
                    map.redraw();
                } catch (IOException e){}
            }
            else
            {
                try {
                    AddLyrDialog aldlg = new AddLyrDialog();
                    aldlg.setMap(map);
                    aldlg.setVisible(true);
                } catch(IOException e){}
            }
        }};
        layercontrollis = new ActionListener() {public void
        actionPerformed(ActionEvent ae){
            String source = ae.getActionCommand();
            System.out.println(activeLayerIndex+" Active Index");
            if (source == "Promote Selected Layer"){
                map.getLayerset().moveLayer(activeLayerIndex,++activeLayerIndex);
                enableDisableButtons();
                map.redraw();
            }
            else if (source == "Promote to Top"){
                int lc = map.getLayerset().getSize();
                map.getLayerset().moveLayer(activeLayerIndex,lc-1);
                enableDisableButtons();
                map.redraw();
            }
            else if(source == "Demote Selected Layer"){
                map.getLayerset().moveLayer(activeLayerIndex,--activeLayerIndex);
                enableDisableButtons();
                map.redraw();}
        }};

        helplis = new ActionListener(){
            public void actionPerformed(ActionEvent ae){
                Object source = ae.getSource();
                if (source instanceof JMenuItem) {
                    String arg = ae.getActionCommand();
                    if(arg == "About MOJO...") {
                        AboutBox aboutbox = new AboutBox();
                        aboutbox.setProductName("MOJO");
                        aboutbox.setProductVersion("2.0");
                        aboutbox.setVisible(true);
                    }
                    else if(arg == "Contact us") {
                        ContactUs cu = new ContactUs();
                        cu.setVisible(true);
                    }
                    else if(arg == "Table of Contents") {
                        try {
                            HelpDialog helpdialog = new HelpDialog((String)helpText.get(0));
                            helpdialog.setVisible(true);
                        } catch(IOException e){}
                    }
                    else if(arg == "Legend Editor") {
                        try {
                            HelpDialog helpdialog = new HelpDialog((String)helpText.get(1));
                            helpdialog.setVisible(true);
                        } catch(IOException e){}
                    }
                    else if(arg == "Layer Control") {
                        try {
                            HelpDialog helpdialog = new HelpDialog((String)helpText.get(2));
                            helpdialog.setVisible(true);
                        } catch(IOException e){}
                    }
                    else if(arg == "Help Tool") {
                        try {
                            HelpDialog helpdialog = new HelpDialog((String)helpText.get(3));
                            helpdialog.setVisible(true);
                        } catch(IOException e){}
                    }
                    else if(arg == "About Project...") {
                        try {
                            HelpDialog helpdialog = new HelpDialog((String)helpText.get(8));
                            helpdialog.setVisible(true);
                        } catch(IOException e){}
                    }
                }
            }};

        layerlis = new ActionListener() {public void actionPerformed(ActionEvent ae){
            Object source = ae.getSource();
            if (source instanceof JMenuItem) {
                String arg = ae.getActionCommand();
                if(arg == "Add Layer") {
                    try {
                        AddLyrDialog aldlg = new AddLyrDialog();
                        aldlg.setMap(map);
                        aldlg.setVisible(true);
                    } catch(IOException e){}
                }
                else if(arg == "Remove Layer") {
                    try {
                        com.esri.mo2.map.dpy.Layer dpylayer =
                                legend.getLayer();
                        map.getLayerset().removeLayer(dpylayer);
                        map.redraw();
                        remlyritem.setEnabled(false);
                        propsitem.setEnabled(false);
                        attribitem.setEnabled(false);
                        promoteitem.setEnabled(false);
                        promoteitemtotop.setEnabled(false);
                        demoteitem.setEnabled(false);
                        stb.setSelectedLayer(null);
                        zptb.setSelectedLayer(null);
                    } catch(Exception e) {}
                }
                else if(arg == "Legend Editor") {
                    LayerProperties lp = new LayerProperties();
                    lp.setLegend(legend);
                    lp.setSelectedTabIndex(0);
                    lp.setVisible(true);
                }
                else if (arg == "Open Attribute Table") {
                    try {
                        layer4 = legend.getLayer();
                        AttrTab attrtab = new AttrTab();
                        attrtab.setVisible(true);
                    } catch(IOException ioe){}
                }
                else if (arg == "Create Layer from Selection") {
                    com.esri.mo2.map.draw.BaseSimpleRenderer sbr = new com.esri.mo2.map.draw.BaseSimpleRenderer();
                    layer4 = legend.getLayer();
                    FeatureLayer flayer2 = (FeatureLayer) layer4;

                    com.esri.mo2.ui.ren.Util utilObj = new com.esri.mo2.ui.ren.Util();
                    int layerType = utilObj.getFeatureType(flayer2);

                    com.esri.mo2.map.draw.SimplePolygonSymbol simplepolysymbol = new com.esri.mo2.map.draw.SimplePolygonSymbol(); //for polygons
                    com.esri.mo2.map.draw.SimpleLineSymbol stSymbol = new com.esri.mo2.map.draw.SimpleLineSymbol(); //for lines
                    com.esri.mo2.map.draw.SimpleMarkerSymbol smSymbol = new com.esri.mo2.map.draw.SimpleMarkerSymbol(); //for points

                    if (layerType == 0) //point
                    {
                        smSymbol.setAntialiasing(true);
                        smSymbol.setTransparency(0.6);
                        smSymbol.setType(com.esri.mo2.map.draw.SimpleMarkerSymbol.TRIANGLE_MARKER);
                        smSymbol.setWidth(12);
                        smSymbol.setSymbolColor(new Color(0, 255, 0));
                    } else if (layerType == 1) //line
                    {
                        stSymbol.setTransparency(0.6);
                        stSymbol.setLineColor(new java.awt.Color(255, 0, 0));
                        stSymbol.setStroke(com.esri.mo2.map.draw.AoLineStyle.getStroke(com.esri.mo2.map.draw.AoLineStyle.DASH_LINE, 4));

                    } else if (layerType == 2) //polygon
                    {
                        simplepolysymbol.setPaint(AoFillStyle.getPaint(com.esri.mo2.map.draw.AoFillStyle.SOLID_FILL, new java.awt.Color(255, 255, 0)));
                        simplepolysymbol.setBoundary(true);
                    }
                    // select, e.g., Montana and then click the
                    // create layer menuitem; next line verifies a selection was made
                    System.out.println("has selected" + flayer2.hasSelection());
                    //next line creates the 'set' of selections
                    if (flayer2.hasSelection()) {
                        SelectionSet selectset = flayer2.getSelectionSet();
                        // next line makes a new feature layer of the selections
                        FeatureLayer selectedlayer = flayer2.createSelectionLayer(selectset);
                        sbr.setLayer(selectedlayer);
                        if (layerType == 0) //point
                        {
                            sbr.setSymbol(smSymbol);
                        } else if (layerType == 1) //line
                        {
                            sbr.setSymbol(stSymbol);
                        } else if (layerType == 2) //polygon
                        {
                            sbr.setSymbol(simplepolysymbol);
                        }
                        selectedlayer.setRenderer(sbr);
                        Layerset layerset = map.getLayerset();
                        // next line places a new visible layer, e.g. Montana, on the map
                        layerset.addLayer(selectedlayer);
                        selectedlayer.setVisible(true);
                        if (stb.getSelectedLayers() != null)
                            promoteitem.setEnabled(true);
                        promoteitemtotop.setEnabled(true);
                        try {
                            legend2 = toc.findLegend(selectedlayer);
                        } catch (Exception e) {
                        }

                        CreateShapeDialog csd = new CreateShapeDialog(selectedlayer);
                        csd.setVisible(true);
                        Flash flash = new Flash(legend2);
                        flash.start();
                        map.redraw(); // necessary to see color immediately

                    }
                }
            }
        }};
        toc.setMap(map);
        mytocadapter = new TocAdapter() {
            public void click(TocEvent e) {
                System.out.println(activeLayerIndex+ "dex");
                legend = e.getLegend();
                activeLayer = legend.getLayer();
                stb.setSelectedLayer(activeLayer);
                zptb.setSelectedLayer(activeLayer);
                // get acive layer index for promote and demote
                activeLayerIndex = map.getLayerset().indexOf(activeLayer);
                // layer indices are in order added, not toc order.
                com.esri.mo2.map.dpy.Layer[] layers = {activeLayer};
                hotlink.setSelectedLayers(layers);
                System.out.println(activeLayerIndex + "Active Index");
                remlyritem.setEnabled(true);
                propsitem.setEnabled(true);
                attribitem.setEnabled(true);
                enableDisableButtons();
            }
        };
        map.addMouseMotionListener(new MouseMotionAdapter() {
            public void mouseMoved(MouseEvent me) {
                com.esri.mo2.cs.geom.Point worldPoint = null;
                if (map.getLayerCount() > 0) {
                    worldPoint = map.transformPixelToWorld(me.getX(),me.getY());
                    String s = "X:"+df.format(worldPoint.getX())+" "+
                            "Y:"+df.format(worldPoint.getY());
                    statusLabel.setText(s);
                }
                else
                    statusLabel.setText("X:0.000 Y:0.000");
            }
        });

        toc.addTocListener(mytocadapter);
        remlyritem.setEnabled(false); // assume no layer initially selected
        propsitem.setEnabled(false);
        attribitem.setEnabled(false);
        promoteitem.setEnabled(false);
        promoteitemtotop.setEnabled(false);
        demoteitem.setEnabled(false);
        printitem.addActionListener(lis);
        addlyritem.addActionListener(layerlis);
        remlyritem.addActionListener(layerlis);
        propsitem.addActionListener(layerlis);
        attribitem.addActionListener(layerlis);
        createlayeritem.addActionListener(layerlis);
        promoteitem.addActionListener(layercontrollis);
        promoteitemtotop.addActionListener(layercontrollis);
        demoteitem.addActionListener(layercontrollis);
        tocitem.addActionListener(helplis);
        legenditem.addActionListener(helplis);
        layercontrolitem.addActionListener(helplis);
        helptoolitem.addActionListener(helplis);
        contactitem.addActionListener(helplis);
        aboutitem.addActionListener(helplis);
        projectitem.addActionListener(helplis);
        file.add(addlyritem);
        file.add(printitem);
        file.add(remlyritem);
        file.add(propsitem);
        theme.add(attribitem);
        theme.add(createlayeritem);
        layercontrol.add(promoteitem);
        layercontrol.add(promoteitemtotop);
        layercontrol.add(demoteitem);
        help.add(helptopics);
        helptopics.add(tocitem);
        helptopics.add(legenditem);
        helptopics.add(layercontrolitem);
        help.add(helptoolitem);
        help.add(contactitem);
        help.add(aboutitem);
        help.add(projectitem);
        mbar.add(file);
        mbar.add(theme);
        mbar.add(layercontrol);
        mbar.add(help);
        prtjb.addActionListener(lis);
        prtjb.setToolTipText("Print Map");
        addlyrjb.addActionListener(lis);
        addlyrjb.setToolTipText("Add Layer");
        ptrjb.addActionListener(lis);
        distjb.addActionListener(lis);
        helpjb.addActionListener(lis);
        helpjb.setToolTipText("Left click here then right click on tool to learn about that tool");
        hotlink.addPickListener(picklis);
        hotlink.setPickWidth(7);// sets tolerance for hotlink clicks
        hotjb.addActionListener(lis);
        hotjb.setToolTipText("Hotlink Tool--click somthing to maybe see a picture");
        XYjb.addActionListener(lis);
        XYjb.setToolTipText("Add a Layer of Points from a File");
        prtjb.setToolTipText("pointer");
        distjb.setToolTipText("Press-Drag-Release to Measure a Distance");
        jtb.add(prtjb);
        jtb.add(addlyrjb);
        jtb.add(ptrjb);
        jtb.add(distjb);
        jtb.add(hotjb);
        jtb.add(XYjb);
        jtb.add(helpjb);
        myjp.add(jtb);
        myjp.add(zptb);
        myjp.add(stb);
        myjp2.add(statusLabel);
        myjp2.add(milesLabel);myjp2.add(kmLabel);
        englishjb.addActionListener(lis);
        englishjb.setToolTipText("select english language");
        spanishjb.addActionListener(lis);
        spanishjb.setToolTipText("select spanish language");
        myjp2.add(englishjb);
        myjp2.add(spanishjb);
        setuphelpText();
        getContentPane().add(map, BorderLayout.CENTER);
        getContentPane().add(myjp,BorderLayout.NORTH);
        getContentPane().add(myjp2,BorderLayout.SOUTH);
        addShapefileToMap(layer,s1);
        addShapefileToMap(layer2,s2);
        getContentPane().add(toc, BorderLayout.WEST);
        java.util.List list = toc.getAllLegends();
        com.esri.mo2.map.dpy.Layer lay2 = ((Legend)list.get(0)).getLayer();
        FeatureLayer fl = (FeatureLayer)lay2;
        BaseSimpleRenderer bsr2 = (BaseSimpleRenderer)fl.getRenderer();
        com.esri.mo2.map.draw.Symbol sym = bsr2.getSymbol();
        RasterMarkerSymbol ttms = new RasterMarkerSymbol();
        ttms.setImageString("Icons/rballoon.jpg");
        ttms.setSizeX(30);
        ttms.setSizeY(30);
        bsr2.setSymbol(ttms);
    }
    private void addShapefileToMap(Layer layer,String s) {
        String datapath = s;
        layer.setDataset("0;"+datapath);
        map.add(layer);
    }

    private void translate() {
        if(file != null || file.equals("")){
            file.setText(names.getString("File"));
        }
        addlyritem.setText(names.getString("AddLayer"));
        remlyritem.setText(names.getString("RemoveLayer"));
        prtjb.setToolTipText(names.getString("Print"));
        addlyrjb.setToolTipText(names.getString("AddLayer"));
        propsitem.setText(names.getString("LegendEditor"));
        printitem.setText(names.getString("Print"));
        theme.setText(names.getString("Theme"));
        help.setText(names.getString("Help"));
        layercontrol.setText(names.getString("LayerControl"));
        ptrjb.setToolTipText(names.getString("Pointer"));
        hotjb.setToolTipText(names.getString("Hotlink"));

    }

    class ContactUs extends JFrame implements ActionListener {
        public ContactUs() {
            JButton ok = new JButton("OK");
            JPanel panel1 = new JPanel();
            JPanel panel2 = new JPanel();
            JLabel centerlabel = new JLabel();
            setBounds(200,100,300,300);
            setTitle("Contact Us");
            ok.addActionListener(new ActionListener() {public void actionPerformed(ActionEvent ae) {
                setVisible(false);
            }});
            String s = "<HTML> <H1>Contact Us:</H1><BR>" +
                    "Sharanya Gangadharan<BR>" +
                    "MS Computer Science,<BR>" +
                    "San Diego State University<BR>" +
                    "5500 Campanile Dr,<BR>" +
                    "San Diego, CA 92182<BR>" +
                    "USA<BR><BR>" +
                    "Email :  sgangadharan@sdsu.edu<BR>";
            centerlabel.setHorizontalAlignment(JLabel.CENTER);
            centerlabel.setText(s);
            panel1.add(centerlabel);
            panel2.add(ok);
            getContentPane().add(panel2,BorderLayout.SOUTH);
            getContentPane().add(panel1,BorderLayout.CENTER);
        }
        public void actionPerformed(ActionEvent e) {this.setVisible(false);
        }
    }

    private void setuphelpText() {
        String s0 = "The toc, or table of contents, is to the left of the map. \n" +
                "Each entry is called a 'legend' and represents a map 'layer' or \n" +
                "'theme'.  If you click on a legend, that layer is called the \n" +
                "active layer, or selected layer.  Its display (rendering) properties \n" +
                "can be controlled using the Legend Editor, and the legends can be \n" +
                "reordered using Layer Control.  Both Legend Editor and Layer Control \n" +
                "are separate Help Topics.  This line is e... x... t... e... n... t... e... d"  +
                "to test the scrollpane.";
        helpText.add(s0);
        String s1 = "The Legend Editor is a menu item found under the File menu. \n" +
                "Given that a layer is selected by clicking on its legend in the table of \n" +
                "contents, clicking on Legend Editor will open a window giving you choices \n" +
                "about how to display that layer.  For example you can control the color \n" +
                "used to display the layer on the map, or whether to use multiple colors ";
        helpText.add(s1);
        String s2 = "Layer Control is a Menu on the menu bar.  If you have selected a \n"+
                "layer by clicking on a legend in the toc (table of contents) to the left of \n" +
                "the map, then the promote and demote tools will become usable.  Clicking on \n" +
                "promote will raise the selected legend one position higher in the toc, and \n" +
                "clicking on demote will lower that legend one position in the toc.";
        helpText.add(s2);
        String s3 = "This tool will allow you to learn about certain other tools. \n" +
                "You begin with a standard left mouse button click on the Help Tool itself. \n" +
                "RIGHT click on another tool and a window may give you information about the  \n" +
                "intended usage of the tool.  Click on the arrow tool to stop using the \n" +
                "help tool.";
        helpText.add(s3);
        String s4 = "If you click on the Zoom In tool, and then click on the map, you \n" +
                "will see a part of the map in greater detail.  You can zoom in multiple times. \n" +
                "You can also sketch a rectangular part of the map, and zoom to that.  You can \n" +
                "undo a Zoom In with a Zoom Out or with a Zoom to Full Extent";
        helpText.add(s4);
        String s5 = "You must have a selected layer to use the Zoom to Active Layer tool.\n" +
                "If you then click on Zoom to Active Layer, you will be shown enough of \n" +
                "the full map to see all of the features in the layer you select.  If you \n" +
                "select a layer that shows where glaciers are, then you do not need to \n" +
                "see Hawaii, or any southern states, so you will see Alaska, and northern \n" +
                "mainland states.";
        helpText.add(s5);
        String s6 = "This tool will help you to measure distance between two points on the map.\n" +
                "If you click on one point and drag to another point you will be able to \n" +
                "see the distance between them in miles as well as in km on the bottom panel.";
        helpText.add(s6);
        String s7 = "The hotlink tool is used to click on the points displayed on the map to get\n" +
                "the information about that point on the map. When you click on this icon\n" +
                "the cursor will change to the hotlink symbol that looks like a bolt.\n" +
                "Now click on any one of the points to get the information window.\n";
        helpText.add(s7);
        String s8 = "This project is about displaying the 19 Google Corporate Offices in\n" +
                "the United States of America. The source of this list is www.google.com. " +
                "The database table has the address of each location, contact numbers, website and business hours.\n" +
                "When clicked through hotlink a picture along with a browser button is displayed for each of them.\n" +
                "While closing the application, a webpage leads to Google.com .";
        helpText.add(s8);

    }
    public static void main(String[] args) {
        QuickStartXY3 qstart = new QuickStartXY3();
        qstart.addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                try {
                    Desktop.getDesktop().browse(new URI("www.google.com"));
                } catch(Exception exp){}
                System.out.println("Thanks, Quick Start exits");
                System.exit(0);
            }
        });
        qstart.setVisible(true);
        env = map.getExtent();
    }
    private void enableDisableButtons() {
        int layerCount = map.getLayerset().getSize();
        if ((layerCount < 2) || activeLayerIndex == -1){
            promoteitem.setEnabled(false);
            promoteitemtotop.setEnabled(false);
            demoteitem.setEnabled(false);
        }
        else if (activeLayerIndex == 0) {
            demoteitem.setEnabled(false);
            promoteitem.setEnabled(true);
            promoteitemtotop.setEnabled(true);
        }
        else if (activeLayerIndex == layerCount - 1) {
            promoteitem.setEnabled(false);
            promoteitemtotop.setEnabled(false);
            demoteitem.setEnabled(true);
        }
        else {
            promoteitem.setEnabled(true);
            promoteitemtotop.setEnabled(true);
            demoteitem.setEnabled(true);
        }
    }
    private ArrayList helpText = new ArrayList(3);
}
// following is an Add Layer dialog window
class AddLyrDialog extends JDialog {
    Map map;
    ActionListener lis;
    JButton ok = new JButton("OK");
    JButton cancel = new JButton("Cancel");
    JPanel panel1 = new JPanel();
    com.esri.mo2.ui.bean.CustomDatasetEditor cus = new com.esri.mo2.ui.bean.CustomDatasetEditor();
    AddLyrDialog() throws IOException {
        setBounds(50,50,520,430);
        setTitle("Select a Theme/Layer");
        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                setVisible(false);
            }
        });
        lis = new ActionListener() {
            public void actionPerformed(ActionEvent ae) {
                Object source = ae.getSource();
                if (source == cancel)
                    setVisible(false);
                else {
                    try {
                        setVisible(false);
                        map.getLayerset().addLayer(cus.getLayer());
                        map.redraw();
                        if (QuickStartXY3.stb.getSelectedLayers() != null)
                            QuickStartXY3.promoteitem.setEnabled(true);
                        QuickStartXY3.promoteitemtotop.setEnabled(true);
                    } catch(IOException e){}
                }
            }
        };
        ok.addActionListener(lis);
        cancel.addActionListener(lis);
        getContentPane().add(cus,BorderLayout.CENTER);
        panel1.add(ok);
        panel1.add(cancel);
        getContentPane().add(panel1,BorderLayout.SOUTH);
    }
    public void setMap(com.esri.mo2.ui.bean.Map map1){
        map = map1;
    }
}
class AddXYtheme extends JDialog {
    Map map;
    Vector s2 = new Vector();
    JFileChooser jfc = new JFileChooser();
    BasePointsArray bpa = new BasePointsArray();
    FeatureLayer XYlayer;
    AddXYtheme() throws IOException {
        setBounds(50,50,520,430);
        jfc.showOpenDialog(this);
        try {
            File file  = jfc.getSelectedFile();
            FileReader fred = new FileReader(file);
            BufferedReader in = new BufferedReader(fred);
            String s; // = in.readLine();
            double x,y;
            int n = 0;
            while ((s = in.readLine()) != null) {
                StringTokenizer st = new StringTokenizer(s,",");
                x = Double.parseDouble(st.nextToken());
                y = Double.parseDouble(st.nextToken());
                bpa.insertPoint(n++,new com.esri.mo2.cs.geom.Point(x,y));
                s2.addElement(st.nextToken());
            }
        } catch (IOException e){}
        XYfeatureLayer xyfl = new XYfeatureLayer(bpa,map,s2);
        XYlayer = xyfl;
        xyfl.setVisible(true);
        map = QuickStartXY3.map;
        map.getLayerset().addLayer(xyfl);
        map.redraw();
        CreateXYShapeDialog xydialog =
                new CreateXYShapeDialog(XYlayer);
        xydialog.setVisible(true);
    }
    public void setMap(com.esri.mo2.ui.bean.Map map1){
        map = map1;
    }
}
class XYfeatureLayer extends BaseFeatureLayer {
    BaseFields fields;
    private java.util.Vector featureVector;
    public XYfeatureLayer(BasePointsArray bpa,Map map,Vector s2) {
        createFeaturesAndFields(bpa,map,s2);
        BaseFeatureClass bfc = getFeatureClass("MyPoints",bpa);
        setFeatureClass(bfc);
        BaseSimpleRenderer srd = new BaseSimpleRenderer();
        SimpleMarkerSymbol sms= new SimpleMarkerSymbol();
        sms.setType(SimpleMarkerSymbol.STAR_MARKER);
        sms.setSymbolColor(new Color(255,0,0));
        sms.setWidth(8);
        srd.setSymbol(sms);
        setRenderer(srd);
        // without setting layer capabilities, the points will not
        // display (but the toc entry will still appear)
        XYLayerCapabilities lc = new XYLayerCapabilities();
        setCapabilities(lc);
    }
    private void createFeaturesAndFields(BasePointsArray bpa,Map map,Vector s2) {
        featureVector = new java.util.Vector();
        fields = new BaseFields();
        createDbfFields();
        for(int i=0;i<bpa.size();i++) {
            BaseFeature feature = new BaseFeature();  //feature is a row
            feature.setFields(fields);
            com.esri.mo2.cs.geom.Point p = new
                    com.esri.mo2.cs.geom.Point(bpa.getPoint(i));
            feature.setValue(0,p);System.out.println(p);
            feature.setValue(1,new Integer(0));  // point data
            feature.setValue(2,(String)s2.elementAt(i));System.out.println((String)s2.elementAt(i));
            feature.setDataID(new BaseDataID("MyPoints",i));
            featureVector.addElement(feature);
        }
    }
    private void createDbfFields() {
        fields.addField(new BaseField("#SHAPE#",Field.ESRI_SHAPE,0,0));
        fields.addField(new BaseField("ID",java.sql.Types.INTEGER,9,0));
        fields.addField(new BaseField("Name",java.sql.Types.VARCHAR,35,0));
        fields.addField(new BaseField("Address",java.sql.Types.VARCHAR,50,0));
        fields.addField(new BaseField("City",java.sql.Types.VARCHAR,15,0));
        fields.addField(new BaseField("State",java.sql.Types.VARCHAR,3,0));
        fields.addField(new BaseField("Zip Code",java.sql.Types.INTEGER,6,0));
        fields.addField(new BaseField("Phone Number",java.sql.Types.VARCHAR,20,0));
        fields.addField(new BaseField("Website",java.sql.Types.VARCHAR,50,0));
        fields.addField(new BaseField("Open Hours",java.sql.Types.VARCHAR,15,0));

    }
    public BaseFeatureClass getFeatureClass(String name,BasePointsArray bpa){
        com.esri.mo2.map.mem.MemoryFeatureClass featClass = null;
        try {
            featClass = new com.esri.mo2.map.mem.MemoryFeatureClass(MapDataset.POINT,
                    fields);
        } catch (IllegalArgumentException iae) {}
        featClass.setName(name);
        for (int i=0;i<bpa.size();i++) {
            featClass.addFeature((Feature) featureVector.elementAt(i));
        }
        return featClass;
    }
    private final class XYLayerCapabilities extends
            com.esri.mo2.map.dpy.LayerCapabilities {
        XYLayerCapabilities() {
            for (int i=0;i<this.size(); i++) {
                setAvailable(this.getCapabilityName(i),true);
                setEnablingAllowed(this.getCapabilityName(i),true);
                getCapability(i).setEnabled(true);
            }
        }
    }
}
class AttrTab extends JDialog {
    JPanel panel1 = new JPanel();
    com.esri.mo2.map.dpy.Layer layer = QuickStartXY3.layer4;
    JTable jtable = new JTable(new MyTableModel());
    JScrollPane scroll = new JScrollPane(jtable);

    public AttrTab() throws IOException {
        setBounds(70,70,450,350);
        setTitle("Attribute Table");
        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                setVisible(false);
            }
        });
        scroll.setHorizontalScrollBarPolicy(
                JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
        // next line necessary for horiz scrollbar to work
        jtable.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);

        TableColumn tc = null;
        int numCols = jtable.getColumnCount();
        //jtable.setPreferredScrollableViewportSize(
        //new java.awt.Dimension(440,340));
        for (int j=0;j<numCols;j++) {
            tc = jtable.getColumnModel().getColumn(j);
            tc.setMinWidth(50);
        }
        getContentPane().add(scroll,BorderLayout.CENTER);
    }
}
class MyTableModel extends AbstractTableModel {
    // the required methods to implement are getRowCount,
    // getColumnCount, getValueAt
    com.esri.mo2.map.dpy.Layer layer = QuickStartXY3.layer4;
    MyTableModel() {
        qfilter.setSubFields(fields);
        com.esri.mo2.data.feat.Cursor cursor = flayer.search(qfilter);
        while (cursor.hasMore()) {
            ArrayList inner = new ArrayList();
            Feature f = (com.esri.mo2.data.feat.Feature)cursor.next();
            inner.add(0,String.valueOf(row));
            for (int j=1;j<fields.getNumFields();j++) {
                inner.add(f.getValue(j).toString());
            }
            data.add(inner);
            row++;
        }
    }
    FeatureLayer flayer = (FeatureLayer) layer;
    FeatureClass fclass = flayer.getFeatureClass();
    String columnNames [] = fclass.getFields().getNames();
    ArrayList data = new ArrayList();
    int row = 0;
    int col = 0;
    BaseQueryFilter qfilter = new BaseQueryFilter();
    Fields fields = fclass.getFields();
    public int getColumnCount() {
        return fclass.getFields().getNumFields();
    }
    public int getRowCount() {
        return data.size();
    }
    public String getColumnName(int colIndx) {
        return columnNames[colIndx];
    }
    public Object getValueAt(int row, int col) {
        ArrayList temp = new ArrayList();
        temp =(ArrayList) data.get(row);
        return temp.get(col);
    }
}
class CreateShapeDialog extends JDialog {
    String name = "";
    String path = "";
    JButton ok = new JButton("OK");
    JButton cancel = new JButton("Cancel");
    JTextField nameField = new JTextField("Enter Layer Name Here, then Hit ENTER",25);
    com.esri.mo2.map.dpy.FeatureLayer selectedlayer;
    ActionListener lis = new ActionListener() {public void actionPerformed(ActionEvent
                                                                                   ae) {
        Object o = ae.getSource();
        if (o == nameField) {
            name = nameField.getText().trim();
            path = ((ShapefileFolder)(QuickStartXY3.layer4.getLayerSource())).getPath();
            System.out.println(path+"    " + name);
        }
        else if (o == cancel)
            setVisible(false);
        else {
            try {
                ShapefileWriter.writeFeatureLayer(selectedlayer,path,name,0);
            } catch(Exception e) {System.out.println("Write Error");}
            setVisible(false);
        }
    }};

    JPanel panel1 = new JPanel();
    JLabel centerlabel = new JLabel();
    //centerlabel;
    CreateShapeDialog (com.esri.mo2.map.dpy.FeatureLayer layer5) {
        selectedlayer = layer5;
        setBounds(40,350,450,150);
        setTitle("Create New Shapefile?");
        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                setVisible(false);
            }
        });
        nameField.addActionListener(lis);
        ok.addActionListener(lis);
        cancel.addActionListener(lis);
        String s = "<HTML> To make a new shapefile from the new layer, enter<BR>" +
                "the new name you want for the layer and click OK.<BR>" +
                "You can then add it to the map in the usual way.<BR>"+
                "Click ENTER after replacing the text with your layer name";
        centerlabel.setHorizontalAlignment(JLabel.CENTER);
        centerlabel.setText(s);
        getContentPane().add(centerlabel,BorderLayout.CENTER);
        panel1.add(nameField);
        panel1.add(ok);
        panel1.add(cancel);
        getContentPane().add(panel1,BorderLayout.SOUTH);
    }
}

class HelpDialog extends JDialog {
    JTextArea helptextarea;
    public HelpDialog(String inputText) throws IOException {
        setBounds(70,70,460,250);
        setTitle("Help");
        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                setVisible(false);
            }
        });
        helptextarea = new JTextArea(inputText,7,40);
        JScrollPane scrollpane = new JScrollPane(helptextarea);
        helptextarea.setEditable(false);
        getContentPane().add(scrollpane,"Center");
    }
}

class HelpTool extends Tool {
}

class CreateXYShapeDialog extends JDialog {
    String name = "";
    String path = "";
    JButton ok = new JButton("OK");
    JButton cancel = new JButton("Cancel");
    JTextField nameField = new JTextField("Enter Layer Name Here, then Hit ENTER",35);
    JTextField pathField = new JTextField("Enter Full Path Name Here, then Hit ENTER",35);
    com.esri.mo2.map.dpy.FeatureLayer XYlayer;
    ActionListener lis = new ActionListener() {public void actionPerformed(ActionEvent
                                                                                   ae) {
        Object o = ae.getSource();
        if (o == pathField) {
            path = pathField.getText().trim();
            System.out.println(path);
        }
        else if (o == nameField) {
            name = nameField.getText().trim();//this works
            //path = ((ShapefileFolder)(QuickStartXY3.layer4.getLayerSource())).getPath();
            System.out.println(path+"    " + name);
        }
        else if (o == cancel)
            setVisible(false);
        else {  // ok button clicked
            try {
                ShapefileWriter.writeFeatureLayer(XYlayer,path,name,0);
                // the following hard-coded line worked with data.csv
                //ShapefileWriter.writeFeatureLayer(XYlayer,"C:\\esri\\moj20\\shapefile","aeroportals",0);
            } catch(Exception e) {System.out.println("Write Error");}
            setVisible(false);
        }
    }};

    JPanel panel1 = new JPanel();
    JPanel panel2 = new JPanel();
    JLabel centerlabel = new JLabel();
    //centerlabel;
    CreateXYShapeDialog (com.esri.mo2.map.dpy.FeatureLayer layer5) {
        XYlayer = layer5;
        setBounds(40,250,600,300);
        setTitle("Create New Shapefile?");
        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                setVisible(false);
            }
        });
        nameField.addActionListener(lis);
        pathField.addActionListener(lis);
        ok.addActionListener(lis);
        cancel.addActionListener(lis);
        String s = "<HTML>To make a new shapefile from the new layer, enter<BR>" +
                "the new name you want for the layer and hit ENTER.<BR>" +
                "then enter a path to the folder you want to use <BR>" +
                "and hit ENTER once again <BR>" + "As an example type C:\\mylayers<BR>" +
                "You can then add it to the map in the usual way.<BR>"+
                "Click ENTER after replacing the text with your layer name";
        centerlabel.setHorizontalAlignment(JLabel.CENTER);
        centerlabel.setText(s);
        //getContentPane().add(centerlabel,BorderLayout.CENTER);
        panel1.add(centerlabel);
        panel1.add(nameField);
        panel1.add(pathField);
        panel2.add(ok);
        panel2.add(cancel);
        getContentPane().add(panel2,BorderLayout.SOUTH);
        getContentPane().add(panel1,BorderLayout.CENTER);
    }
}
class Arrow extends Tool {

    Arrow() { // undo measure tool residue
        QuickStartXY3.milesLabel.setText("DIST   0 mi   ");
        QuickStartXY3.kmLabel.setText("   0 km    ");
        //QuickStartXY3.map.remove(QuickStartXY3.acetLayer);
        //QuickStartXY3.acetLayer = null;
        QuickStartXY3.map.repaint();
    }
}
class Flash extends Thread {
    Legend legend;
    Flash(Legend legendin) {
        legend = legendin;
    }
    public void run() {
        for (int i=0;i<12;i++) {
            try {
                Thread.sleep(500);
                legend.toggleSelected();
            } catch (Exception e) {}
        }
    }
}
class DistanceTool extends DragTool  {
    int startx,starty,endx,endy,currx,curry;
    com.esri.mo2.cs.geom.Point initPoint, endPoint, currPoint;
    double distance;
    public void mousePressed(MouseEvent me) {
        startx = me.getX(); starty = me.getY();
        initPoint = QuickStartXY3.map.transformPixelToWorld(me.getX(),me.getY());
    }
    public void mouseReleased(MouseEvent me) {
        // now we create an acetatelayer instance and draw a line on it
        endx = me.getX(); endy = me.getY();
        endPoint = QuickStartXY3.map.transformPixelToWorld(me.getX(),me.getY());
        distance = (69.44 / (2*Math.PI)) * 360 * Math.acos(
                Math.sin(initPoint.y * 2 * Math.PI / 360)
                        * Math.sin(endPoint.y * 2 * Math.PI / 360)
                        + Math.cos(initPoint.y * 2 * Math.PI / 360)
                        * Math.cos(endPoint.y * 2 * Math.PI / 360)
                        * (Math.abs(initPoint.x - endPoint.x) < 180 ?
                        Math.cos((initPoint.x - endPoint.x)*2*Math.PI/360):
                        Math.cos((360 - Math.abs(initPoint.x -
                                endPoint.x))*2*Math.PI/360)));
        System.out.println( distance  );
        QuickStartXY3.milesLabel.setText("DIST: " + new Float((float)distance).toString() + " mi  ");
        QuickStartXY3.kmLabel.setText(new Float((float)(distance*1.6093)).toString() + "km");
        if (QuickStartXY3.acetLayer != null)
            QuickStartXY3.map.remove(QuickStartXY3.acetLayer);
        QuickStartXY3.acetLayer = new AcetateLayer() {
            public void paintComponent(java.awt.Graphics g) {
                java.awt.Graphics2D g2d = (java.awt.Graphics2D) g;
                Line2D.Double line = new Line2D.Double(startx,starty,endx,endy);
                g2d.setColor(new Color(0,0,250));
                g2d.draw(line);
            }
        };
        Graphics g = super.getGraphics();
        QuickStartXY3.map.add(QuickStartXY3.acetLayer);
        QuickStartXY3.map.redraw();
    }
    public void cancel() {};
}

class HotPick extends JDialog {
    String reserveUrl = "";
    String mystate = QuickStartXY3.mystate;
    String stat = QuickStartXY3.stat;
    String myloc = null;
    String mylocpic = null;
    JPanel jpanel = new JPanel();
    JPanel jpanel2 = new JPanel();
    JButton button = new JButton("More Details");
    String[][] goolocs={{"WA","Kirkland","Icons/kirk.jpg","https://careers.google.com/locations/seattle-kirkland/"},{"WA","Seattle","Icons/seattle.jpg","https://careers.google.com/locations/seattle-kirkland/"},
            {"OR","Portland","Icons/portland.jpg","https://careers.google.com/locations/"},{"CA","San Francisco","Icons/sanfran.jpg","https://careers.google.com/locations/san-francisco/"},
            {"CA","Mountain View","Icons/MountView.jpg","https://careers.google.com/locations/mountain-view/"},{"CA","Los Angeles","Icons/la.jpg","https://careers.google.com/locations/los-angeles/"},
            {"CA","Orange County","Icons/irvine.jpg","https://careers.google.com/locations/orange-county/"},{"TX","Austin","Icons/austin.jpeg","https://careers.google.com/locations/"},
            {"CO","Boulder","Icons/boulder.jpg","https://careers.google.com/locations/boulder/"},{"GA","Atlanta","Icons/atlanta.jpg","https://careers.google.com/locations/"},
            {"WI","Madison","Icons/madison.jpg","https://www.google.com/about/locations/madison/"},{"IL","Chicago","Icons/chicago.jpg","https://careers.google.com/locations/"},
            {"MI","Ann Arbor","Icons/ann.jpg","https://www.google.com/about/locations/ann-arbor/"},{"MI","Birmingham","Icons/birm.jpg","https://careers.google.com/locations/"},
            {"PA","Pittsburgh","Icons/pittsb.png","https://www.google.com/intl/en/about/careers/locations/pittsburgh/"},{"MA","Cambridge","Icons/cam.jpg","https://careers.google.com/locations/cambridge/"},
            {"NY","New York","Icons/nyc.jpg","https://www.google.com/about/locations/new-york/"},{"DC","Washington","Icons/dc.jpg","https://careers.google.com/locations/"},
            {"VA","Reston","Icons/reston.jpg","https://careers.google.com/locations/"}};
    HotPick() throws IOException {
        setTitle("Google "+mystate);
        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                setVisible(false);
            }
        });
        int width;
        int height;
        mystate = mystate.trim();
        for (int i = 0; i < 19; i++) {
            if (goolocs[i][1].equals(mystate)) {
                myloc = goolocs[i][0];
                mylocpic = goolocs[i][2];
                reserveUrl = goolocs[i][3];
                break;
            }
        }
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent ae) {
                System.out.println(mystate);
                String url = reserveUrl;
                try {
                    Desktop.getDesktop().browse(new URI(url));
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (URISyntaxException e) {
                    e.printStackTrace();
                }
            }});
        BufferedImage bimg = ImageIO.read(new File(mylocpic));
        width = bimg.getWidth();
        height = bimg.getHeight();
        setBounds(500, 500, width, height);
        JLabel label = new JLabel(mystate+", ");
        JLabel label2 = new JLabel(myloc);
        ImageIcon locIcon = new ImageIcon(mylocpic);
        JLabel locLabel = new JLabel(locIcon);
        jpanel2.add(locLabel);
        jpanel.add(label);
        jpanel.add(label2);
        jpanel.add(button);
        getContentPane().add(jpanel2,BorderLayout.CENTER);
        getContentPane().add(jpanel,BorderLayout.SOUTH);
    }
}