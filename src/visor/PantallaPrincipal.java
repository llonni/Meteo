package visor;

import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileInputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.table.DefaultTableModel;
import javax.xml.parsers.ParserConfigurationException;

import org.xml.sax.SAXException;

import servidor.ConexionProveedor;
import servidor.DatosMeteo;

//Import log4j classes.
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.core.config.ConfigurationSource;
import org.apache.logging.log4j.LogManager;

public class PantallaPrincipal extends JFrame implements ActionListener{
    // Define a static logger variable so that it references the
    // Logger instance named "MyApp".
    private static final Logger log = LogManager.getLogger(PantallaPrincipal.class);
    
	ConexionProveedor conProv=null;
	DatosMeteo datosMeteo=null;
	
    private JButton boton;          // boton con una determinada accion

    private JLabel labelLocalidad;
    private JPanel panelResultados;
    
    GridBagLayout mGridBagLayoutPrincipal;
    GridBagLayout mGridBagLayoutResultados;
    GridBagConstraints constraints;
    JScrollPane sc;
    
    class PanelBloqueResultado{
    	JPanel panel = new JPanel(); // Un panel para cada bloque de resultados
    	JLabel hora = new JLabel();
    	JLabel cielo = new JLabel();
    	JLabel precipTxt = new JLabel();
    	JLabel precipValue = new JLabel();
    	JLabel temperatura = new JLabel();
    }

    class PanelDiaResultados {
    	PanelBloqueResultado bloque[];
    	JLabel dia = new JLabel();
    }
    
    PanelDiaResultados[] panelDiaResultados;

	public PantallaPrincipal() {
		super(); 
		
        configurarVentana();        // configuramos la ventana
        inicializarComponentes();   // inicializamos los atributos o componentes
        
        cargaPrevision();
	}
	
	private void configurarVentana() {
        this.setTitle("Previsión Meteorológica");                   // colocamos titulo a la ventana
        this.setSize(1200, 700);                                 // colocamos tamanio a la ventana (ancho, alto)
        this.setLocationRelativeTo(null);                       // centramos la ventana en la pantalla
        //this.setLayout(null);                                   // no usamos ningun layout, solo asi podremos dar posiciones a los componentes
        this.setResizable(true);                               // hacemos que la ventana sea redimiensionable
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);    // hacemos que cuando se cierre la ventana termina todo proceso
    }
	
    private void inicializarComponentes() {
    	// Se instancia un objeto layout de tipo GridLayout para ser
    	mGridBagLayoutPrincipal = new GridBagLayout();
    	this.getContentPane().setLayout (mGridBagLayoutPrincipal);
        constraints = new GridBagConstraints();

        constraints.insets = new Insets(10,10,10,10);
        
        labelLocalidad = new JLabel();
        labelLocalidad.setText("Localidad");
        constraints.gridx = 0; // El área de texto empieza en la columna cero.
        constraints.gridy = 0; // El área de texto empieza en la fila cero
        constraints.gridwidth = 1; // El área de texto ocupa dos columnas.
        constraints.gridheight = 1; // El área de texto ocupa 2 filas.
        constraints.weightx = 1.0;
        constraints.weighty = 0.0;
        constraints.fill = GridBagConstraints.NONE;
        constraints.anchor = GridBagConstraints.CENTER;
        this.getContentPane().add (labelLocalidad, constraints);

        panelResultados = new JPanel();
        constraints.gridx = 0; // El área de texto empieza en la columna cero.
        constraints.gridy = 1; // El área de texto empieza en la fila cero
        constraints.gridwidth = 1; // El área de texto ocupa dos columnas.
        constraints.gridheight = 1; // El área de texto ocupa 2 filas.
        constraints.weightx = 1.0;
        constraints.weighty = 1.0;
        constraints.fill = GridBagConstraints.BOTH;
        constraints.anchor = GridBagConstraints.CENTER;        
        sc = new JScrollPane(panelResultados);
        this.getContentPane().add (sc, constraints);
        
    	mGridBagLayoutResultados = new GridBagLayout();
    	panelResultados.setLayout(mGridBagLayoutResultados);
    	
    	constraints.insets = new Insets(5,5,5,5);
    	
    	// Inicializamos estructura paneles de resultados
    	panelDiaResultados = new PanelDiaResultados[6];
    	for (int i=0 ; i< panelDiaResultados.length; i++)    
    	{	
    		panelDiaResultados[i] = new PanelDiaResultados();    
    		
    		// Colocamos la eqtiqueta dia en el panel de resultados global
    		panelDiaResultados[i].dia.setText("");
	        constraints.gridx = 0; // El área de texto empieza en la columna cero.
	        constraints.gridy = i*2; // El área de texto empieza en la fila cero
	        constraints.gridwidth = 1; // El área de texto ocupa dos columnas.
	        constraints.gridheight = 1; // El área de texto ocupa 2 filas.
	        constraints.weightx = 1.0;
	        constraints.weighty = 1.0;
	        constraints.fill = GridBagConstraints.BOTH;
	        constraints.anchor = GridBagConstraints.CENTER;        
	        //sc = new JScrollPane(panelDiaResultados[i].bloque[j].panel);
	        panelResultados.add (panelDiaResultados[i].dia, constraints);
    		
    		panelDiaResultados[i].bloque = new PanelBloqueResultado[8];
    		for (int j=0 ; j< panelDiaResultados[i].bloque.length; j++) 
    		{	
    			panelDiaResultados[i].bloque[j] = new PanelBloqueResultado();
    			
    			panelDiaResultados[i].bloque[j].panel.setLayout(new BoxLayout(panelDiaResultados[i].bloque[j].panel, BoxLayout.PAGE_AXIS));
    		
    			// colocamos cada panel bloque en el panel de resultados global     DENTRO DEL BLOQUE DE MOMENTO NO GRIDBAGLAYOUT
    	        constraints.gridx = j; // El área de texto empieza en la columna cero.
    	        constraints.gridy = i*2 +1; // El área de texto empieza en la fila cero
    	        constraints.gridwidth = 1; // El área de texto ocupa dos columnas.
    	        constraints.gridheight = 1; // El área de texto ocupa 2 filas.
    	        constraints.weightx = 1.0;
    	        constraints.weighty = 1.0;
    	        constraints.fill = GridBagConstraints.BOTH;
    	        constraints.anchor = GridBagConstraints.CENTER;        
    	        sc = new JScrollPane(panelDiaResultados[i].bloque[j].panel);
    	        //this.getContentPane().add (sc, constraints);
    	        panelResultados.add (sc, constraints);
    	        
    			// colocamos cada etiqueta en cada bloque       
    	        panelDiaResultados[i].bloque[j].panel.add(panelDiaResultados[i].bloque[j].hora);
    	        panelDiaResultados[i].bloque[j].panel.add(panelDiaResultados[i].bloque[j].cielo);
    	        panelDiaResultados[i].bloque[j].panel.add(panelDiaResultados[i].bloque[j].precipTxt);
    	        panelDiaResultados[i].bloque[j].panel.add(panelDiaResultados[i].bloque[j].precipValue);
    	        panelDiaResultados[i].bloque[j].panel.add(panelDiaResultados[i].bloque[j].temperatura);   		
    		}
    	}
    	
    	for (int x=0; x<6; x++)
    	{	
    		//posiciona dia i
    		
    		for (int y=0; y<8; y++)
    		{
    			//posiciona bloque xy
    	        panelResultados = new JPanel();
    	        constraints.gridx = 0; // El área de texto empieza en la columna cero.
    	        constraints.gridy = 1; // El área de texto empieza en la fila cero
    	        constraints.gridwidth = 1; // El área de texto ocupa dos columnas.
    	        constraints.gridheight = 1; // El área de texto ocupa 2 filas.
    	        constraints.weightx = 1.0;
    	        constraints.weighty = 1.0;
    	        constraints.fill = GridBagConstraints.BOTH;
    	        constraints.anchor = GridBagConstraints.CENTER;        
    	        sc = new JScrollPane(panelResultados);
    	        this.getContentPane().add (sc, constraints);
    	        
    	    	mGridBagLayoutResultados = new GridBagLayout();
    	    	panelResultados.setLayout(mGridBagLayoutResultados);
    			
    		}
    	}
    }
    
    public void cargaPrevision() {
		conProv = new ConexionProveedor();
		int indiceDiaPrevision=0;
		SimpleDateFormat sdf;
		int fila=0;
		int columna=0;

		try {
			datosMeteo = conProv.getMeteoPrevision();
		} catch (ParserConfigurationException | ParseException | SAXException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			System.exit(-1);
		}
		
		if (datosMeteo==null) {
			JOptionPane.showMessageDialog(this, "La consulta no ha devuelto resultados");
			return;
		}

		////////////////////////////
		// Carga valores en ventana
		///////////////////////////
		// TITULO
		this.labelLocalidad.setText(datosMeteo.localidad + "  (" +  datosMeteo.pais + ")    Lat: " + 
				Float.toString(datosMeteo.posicion.lat).replace(".", ",") + "  Long: " + Float.toString(datosMeteo.posicion.lon).replace(".", ","));
		
		
		// Inicializamos indice para recorrer estructura de pantalla de resultados
		//... indicedia = 1er dia y luego se indexa [diaActual-indiceDia]
		sdf = new SimpleDateFormat("d");
		indiceDiaPrevision = Integer.parseInt(sdf.format(datosMeteo.dia[0].fechaDesde));
		
		
		// Recorremos cada bloque de resultados y copiamos valores
		
		for (int i=0; i< datosMeteo.dia.length; i++) {
			sdf = new SimpleDateFormat("d");
			fila = Integer.parseInt(sdf.format(datosMeteo.dia[i].fechaDesde)) - indiceDiaPrevision;
			sdf = new SimpleDateFormat("H");
			columna = Math.floorDiv(Integer.parseInt(sdf.format(datosMeteo.dia[i].fechaDesde)) , 3);
			
			sdf = new SimpleDateFormat("dd/MM/yyyy");
			panelDiaResultados[fila].dia.setText(" Día " + sdf.format(datosMeteo.dia[i].fechaDesde)); // se repite en todos los bloques de la fila... no está optimizado

			panelDiaResultados[fila].bloque[columna].precipTxt.setText(datosMeteo.dia[i].precipitaciones.tipo.toUpperCase());
			panelDiaResultados[fila].bloque[columna].precipValue.setText("Ppm " + Float.toString(datosMeteo.dia[i].precipitaciones.valor).replace(".", ","));
			panelDiaResultados[fila].bloque[columna].temperatura.setText("T " + Float.toString(datosMeteo.dia[i].temperatura.media).replace(".", ",") + "º" );
			sdf = new SimpleDateFormat("HH");
			panelDiaResultados[fila].bloque[columna].hora.setText(
					"" + sdf.format(datosMeteo.dia[i].fechaDesde) + "h-" + sdf.format(datosMeteo.dia[i].fechaHasta) + "h");
			panelDiaResultados[fila].bloque[columna].cielo.setText(datosMeteo.dia[i].cielo.descripcion);
		}
			
		
		//this.putInfoPantalla(datosMeteo);
   
    }
 
	public void putInfoPantalla(DatosMeteo datosMeteo) {
		SimpleDateFormat sdf;
        String blancos = "                 ";
        /*
        Calendar cDesde = Calendar.getInstance();
        Calendar cHasta = Calendar.getInstance();
        cDesde.setTime(datosMeteo.dia[temp].fechaDesde);
        cHasta.setTime(datosMeteo.dia[temp].fechaHasta);
        */
        
        for (int temp = 0; temp < datosMeteo.dia.length; temp++) {
        
	        /////// FORMAT ////////////// date TO string
	        sdf = new SimpleDateFormat("dd");
	        String fecha = sdf.format(datosMeteo.dia[temp].fechaDesde);
	        sdf = new SimpleDateFormat("H");
	        String hDesde = sdf.format(datosMeteo.dia[temp].fechaDesde);
	        String hHasta = sdf.format(datosMeteo.dia[temp].fechaHasta);
	        
	        System.out.println(
	        		datosMeteo.localidad + " " +
	        //		"(" + datosMeteo.pais + ")" +
	        		fecha + " " + hDesde + "h-" + hHasta + "h " + 
	        		datosMeteo.dia[temp].cielo.descripcion + " " + 
	        				blancos.substring(0,blancos.length()-datosMeteo.dia[temp].cielo.descripcion.length()) +
	        		datosMeteo.dia[temp].precipitaciones.tipo.toUpperCase() + ": " + datosMeteo.dia[temp].precipitaciones.valor + 
	        				blancos.substring(0,6-Float.toString(datosMeteo.dia[temp].precipitaciones.valor).length()) + " Tª[" +
	        		datosMeteo.dia[temp].temperatura.min +
	        			blancos.substring(0,5-Float.toString(datosMeteo.dia[temp].temperatura.min).length()) +
	        			" (" + datosMeteo.dia[temp].temperatura.media +") " +
	        			blancos.substring(0,5-Float.toString(datosMeteo.dia[temp].temperatura.media).length()) +
	        			datosMeteo.dia[temp].temperatura.max + "]"
	        		);	
        }	
	}

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		PantallaPrincipal pantalla = new PantallaPrincipal();
		pantalla.setVisible(true);
		
        log.trace("trace message");
        log.debug("debug message");
        log.warn("warn message");
        log.info("info message");
        log.error("error message");
        log.fatal("fatal message");
	     
        
		//System.exit(0);
	}

	@Override
	public void actionPerformed(ActionEvent arg0) {
		// TODO Auto-generated method stub
		
	}

}
