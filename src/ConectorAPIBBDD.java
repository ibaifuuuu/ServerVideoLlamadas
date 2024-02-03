import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.rmi.RemoteException;

import interfaces.interfazRMI;

public class ConectorAPIBBDD implements interfazRMI {
	
	public String getAPI(String urlParaVisitar) throws RemoteException {
		StringBuilder resultado = new StringBuilder();
		try {
			URL url = new URL(urlParaVisitar);
			HttpURLConnection conexion = (HttpURLConnection) url.openConnection();
			conexion.setRequestMethod("GET");
			BufferedReader rd = new BufferedReader(new InputStreamReader(conexion.getInputStream()));
			String linea;
			while ((linea = rd.readLine()) != null) {
				resultado.append(linea);
			}
			rd.close();

		} catch (Exception e) {
			e.printStackTrace();
		}

		return resultado.toString();
	}
	
	public static String postAPI(String urlParaVisitar, String jsonInputString) throws RemoteException {
		URL url;
		StringBuilder resultado = new StringBuilder();

		try {
			url = new URL(urlParaVisitar);
			HttpURLConnection conexion = (HttpURLConnection) url.openConnection();
			conexion.setRequestMethod("POST");
			conexion.setDoOutput(true);
			conexion.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
			OutputStreamWriter writer = new OutputStreamWriter(conexion.getOutputStream(), "UTF-8");
			writer.write(jsonInputString);
			writer.close();

			BufferedReader rd = new BufferedReader(new InputStreamReader(conexion.getInputStream()));
			String linea;
			while ((linea = rd.readLine()) != null) {
				resultado.append(linea);
			}
			rd.close();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return resultado.toString();
	}

}
