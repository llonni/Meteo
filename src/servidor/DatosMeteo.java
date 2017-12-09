package servidor;

import java.util.Date;

public class DatosMeteo {
	public String localidad;
	public String pais;
	public float lat;
	public float lon;
	public Posicion posicion = new Posicion();
	public Sol sol = new Sol();
	public Dia dia[];
	
	public class Sol{
		public Date orto;
		public Date ocaso;
	}
	public class Posicion{
		public float lat;
		public float lon;
	}
	public class Dia{
		public Date fechaDesde;
		public Date fechaHasta;
		public Precipitaciones precipitaciones = new Precipitaciones();
		public Viento viento = new Viento();
		public Temperatura temperatura = new Temperatura();
		public float presion;
		public float humedadPorcentaje;
		public Cielo cielo = new Cielo();
	}	
	
	public class Viento{
		public String direccion;
		public float velocidad;
	}
	public class Temperatura{
		public float media;
		public float min;
		public float max;
	}
	public class Cielo{
		public String descripcion;
		public float porcentaje;
	}
	public class Precipitaciones{
		public float valor;
		public String tipo;
	}
	
	public DatosMeteo(int dias) {
		// TODO Auto-generated constructor stub
		this.dia = new Dia[dias];
		for (int i=0;i<dias;i++) 
			this.dia[i] = new Dia(); 
		
	}

}
