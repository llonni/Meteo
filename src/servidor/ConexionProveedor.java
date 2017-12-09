package servidor;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import visor.PantallaPrincipal;

//Import log4j classes.
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;

public class ConexionProveedor {
	
	static final Logger log = LogManager.getLogger(ConexionProveedor.class);
	//MyApp uses the Bar class defined in the package com.foo.
	//private static final Logger logger = LogManager.getLogger(Bar.class.getName());
	
	URL peticion = null;
	
	public DatosMeteo getMeteoPrevision() throws ParserConfigurationException, ParseException, SAXException {
		// TODO Auto-generated method stub
		StringBuffer tvResult= new StringBuffer();
		Date horaDesde=null;
		Date horaHasta=null;
		
		DatosMeteo datosMeteo = null;
		
		//System.out.println(ConexionProveedor.class.getName());
		
		try {
			  peticion = new 
			  URL("http://api.openweathermap.org/data/2.5/forecast?q=tres%20cantos,es&mode=xml&units=metric&lang=es&appid=c18c521d5d80ecc82e8e33e8601394a7"); 

			  		     
			  // Abrimos la conexión     
			  URLConnection tc = peticion.openConnection();
			   
			  // Obtenemos la respuesta del servidor
			  //BufferedReader in = new BufferedReader(new InputStreamReader( tc.getInputStream()));
			  
			    DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
			    DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
			    Document doc = null;
			    doc = dBuilder.parse(tc.getInputStream());

			    doc.getDocumentElement().normalize();

			    //System.out.println("Root element :" + doc.getDocumentElement().getNodeName());

			    NodeList nList = doc.getElementsByTagName("time");
			    
			    

			    //System.out.println("----------- Nº elementos: " + nList.getLength() + " ----------------- ");
			    datosMeteo = new DatosMeteo(nList.getLength());
			    
			    //Cargamos datos comunes
			    NodeList nList2 = doc.getElementsByTagName("location");
			    Node nNode2 = nList2.item(0);
			    for (Node child = nNode2.getFirstChild(); child != null; child = child.getNextSibling()) {
			    	if (child.getNodeName() == "name") {
			    		//datosMeteo.localidad = nNode2.getAttributes().getNamedItem("name").getNodeValue();
			    		datosMeteo.localidad = child.getTextContent();
			    	}	
			    	if (child.getNodeName() == "country") {
			    		datosMeteo.pais = child.getTextContent();
			    	}
			    	if (child.getNodeName() == "location") {
			    		datosMeteo.posicion.lat = Float.parseFloat(child.getAttributes().getNamedItem("latitude").getNodeValue());
			    		datosMeteo.posicion.lon = Float.parseFloat(child.getAttributes().getNamedItem("longitude").getNodeValue());
			    	}
			    }

			    for (int temp = 0; temp < nList.getLength(); temp++) {

			        Node nNode = nList.item(temp);
			        
			        //System.out.println("\nCurrent Element :" + nNode.getNodeName());
			        //System.out.println("From : " + nNode.getAttributes().getNamedItem("from").getNodeValue());
			        
			        
			        if (nNode.getNodeType() == Node.ELEMENT_NODE) {

			            Element eElement = (Element) nNode;

			            //System.out.println("from : " + eElement.getAttribute("from") + " to : " + eElement.getAttribute("to"));
			            
			            //SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			            SimpleDateFormat sdf;
			            
			            String stringFecha;
			            
			            stringFecha = eElement.getAttribute("from").toString();
			            String stringHoraDesde = stringFecha.replace("T", " ");
			            stringFecha = eElement.getAttribute("to").toString();
			            String stringHoraHasta = stringFecha.replace("T", " ");
			            
			            ////////// PARSE //////////////   string TO date
			            sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			            datosMeteo.dia[temp].fechaDesde = sdf.parse(stringHoraDesde);
			            datosMeteo.dia[temp].fechaHasta = sdf.parse(stringHoraHasta);
			            
			            
			            
			            for (Node child = nNode.getFirstChild(); child != null; child = child.getNextSibling()) {
			            	
			            	if (child.getNodeName() == "clouds") {
			            		//System.out.println(child.getNodeName() + " " + child.getAttributes().getNamedItem("value").getNodeValue());
			            		datosMeteo.dia[temp].cielo.descripcion = child.getAttributes().getNamedItem("value").getNodeValue();
			            	}
			            	if (child.getNodeName() == "temperature") {
			            		datosMeteo.dia[temp].temperatura.max = 
			            				Float.parseFloat(child.getAttributes().getNamedItem("max").getNodeValue());
			            		datosMeteo.dia[temp].temperatura.media = 
			            				Float.parseFloat(child.getAttributes().getNamedItem("value").getNodeValue());
			            		datosMeteo.dia[temp].temperatura.min = 
			            				Float.parseFloat(child.getAttributes().getNamedItem("min").getNodeValue());
			            	}
			            	if (child.getNodeName() == "precipitation") {
			            		if (child.getAttributes().getNamedItem("value") != null) {
			            			//System.out.println(child.getNodeName() + " " + child.getAttributes().getNamedItem("value").getNodeValue());
			            			datosMeteo.dia[temp].precipitaciones.valor = Float.parseFloat(child.getAttributes().getNamedItem("value").getNodeValue());
			            			datosMeteo.dia[temp].precipitaciones.tipo = child.getAttributes().getNamedItem("type").getNodeValue();
			            		}
			            		else {
			            			//System.out.println(child.getNodeName() + "  0");
			            			datosMeteo.dia[temp].precipitaciones.valor = 0;
			            			datosMeteo.dia[temp].precipitaciones.tipo = "N/A ";
			            		}
			            	}
			            }

			        }
			        
			    }
					        
			        
			        
			  
			 /* 
			  String line;
			      
			  // Leemos la respuesta del servidor y la imprimimos 
			  while ((line = in.readLine()) != null) {
			       tvResult.append(line);
			  }
			  in.close();
			  System.out.println(tvResult.toString()); 
			  */
			  
			  
			} catch (MalformedURLException e) {
			       e.printStackTrace();
			} catch (IOException e) {
			       e.printStackTrace();
			}
		
		return datosMeteo; 
	}

	public ConexionProveedor() {
		// TODO Auto-generated constructor stub
	}


}
