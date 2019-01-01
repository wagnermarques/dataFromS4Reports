package br.gov.saude.sp.ipgg;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.Node;
import org.jsoup.select.Elements;

import java.io.*;
import java.util.Properties;

public class App {

	private static File htmlFile;
	private static Document htmlDocument;
	private static Element body;

	private static FileWriter fw;

	private static String dtaIni = "";
	private static String dtaFim = "";
	private static String Emissao = "";

	private static String PacPront = "";
	private static String PacNome = "";
	private static String Idade = "";
	private static String Item = "";
	private static String Qtde = "";
	private static String Realizacao = "";
	private static String NumAdminssao = "";
	private static String TpoAdmissao = "";
	private static String Responsavel = "";
	private static String Complemento = "";

	private static int ln = 0, pac = 0;

	private static boolean pacNovo = true;
	private static boolean pacIdentificado = false;
	private static boolean linhaLida = false;
	private static int noDeDecisaoDeNovoPac = 0;
	private static boolean proximoHeOComplDeVerdade = false;
	private static int numNosDeMudancaDePagina = 0;
	private static boolean identificadaNovaPagina = false;
	private static boolean acheiONodeSUS = false;
	private static boolean detectadaMudancaDePaginaDuranteProcuraDoComplemento = false;
	private static boolean comecouProcuraPeloComplemento = false;
	
	static Properties properties;
	
	public static void main(String[] args) throws IOException {
		ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
		InputStream configPropAsStream = classLoader.getResourceAsStream("config.properties");		
		
		Properties props = new Properties();		
		props.load(configPropAsStream); 
		
		String htmlFilePath = props.getProperty("fileDirPath")+ "/" +props.getProperty("fileNameInput");
		String csvFilePath = props.getProperty("fileDirPath")+ "/" +props.getProperty("fileNameOutput");		

                htmlFile = new File(htmlFilePath);
                
		transformToCsv(htmlFile,csvFilePath);
	}

	public static void transformToCsv(File htmlFile, String csvOutputFileName) throws IOException{
		
		htmlDocument = Jsoup.parse(htmlFile, "UTF-8");
		
		body = htmlDocument.body();

		fw = new FileWriter(csvOutputFileName);
		fw.write(getNomeDasColunas() + "\n");
		
		getDataDeReferenciaDoRelatorio();
		getData(67);
		fw.close();		
	}
		
	private static void getDataDeReferenciaDoRelatorio() {
		int i = 0;

		for (Node node : body.childNodes()) {
			i++;
			if (i == 14)
				dtaIni = node.toString().trim();
			if (i == 19)
				dtaFim = node.toString().trim();
			if (i == 23)
				Emissao = node.toString().trim();
		}
	}

	private static void getData(int no) throws IOException {
		int i = 0;

		for (Node node : body.childNodes()) {
			
			i++;
			//System.out.println(i + " | "+node.toString());
			
			if (i == noDeDecisaoDeNovoPac) {// 89				
				if(node.toString().trim().equals("REG0010R") || identificadaNovaPagina == true) {
					//o no de decisao de qualquer maneira he o proximo caso entre aqui
					identificadaNovaPagina = true;
					noDeDecisaoDeNovoPac = i+1;
					
					if(node.toString().trim().equals("SUS")) {
						acheiONodeSUS = true;
						numNosDeMudancaDePagina = 0; 
					}
					
					if(acheiONodeSUS == true) {	
					//inicio de mudanca de pagina
					//78 nos pra chegar no novo pac
						numNosDeMudancaDePagina++;
						
						if(numNosDeMudancaDePagina <= 29) {
						
						}else {
							numNosDeMudancaDePagina = 500;
							identificadaNovaPagina = false;
							pacNovo = true;
							pacIdentificado = false;
							acheiONodeSUS = false;
							noDeDecisaoDeNovoPac = i-1;
							no = i;
						}
					}
				//System.out.println("no + 20 = " + (no + 22) + " - " + node.toString().trim());
				}else if(detectadaMudancaDePaginaDuranteProcuraDoComplemento == true && primeiroCaractereDoNodeDeDecisaoNAOHENumero(node)) {
					pacNovo = false;
					pacIdentificado = true;
					noDeDecisaoDeNovoPac = i+2;
					no = i - 2;	
					detectadaMudancaDePaginaDuranteProcuraDoComplemento = false;
				}else if (isItAIntegerEMenorQue150(node.toString().trim())) {
					pacNovo = false;
					no = i - 4;
				}else {
					pacNovo = true;
					pacIdentificado = false;
					no = i;
				}
			}

			if (pacNovo) {
				if (i == no) {
					PacPront = node.toString().trim();
					//System.out.println(ln + " " + PacPront);
				}
				if (i == (no + 2)) {// 69
					// pac++; aqui comeca os dados do pac
					// mas so depois que eu coletar todos os dados dele eu faco pac++
					PacNome = node.toString().replaceAll("&nbsp;", "").trim();
					//System.out.println(ln + " " + PacNome);
					pacIdentificado = true;
				}
			}
			if (pacIdentificado) {
				linhaLida = obtemDadosDoPac(i, no + 4, node);
				if (linhaLida) {
					ln++;
					escreveLinha(ln, pac, dtaIni, dtaFim, Emissao, PacPront, PacNome, Idade, Item, Qtde, Realizacao,
							NumAdminssao, TpoAdmissao, Responsavel, Complemento);

				}
				}
		}
	}

	private static boolean primeiroCaractereDoNodeDeDecisaoNAOHENumero(Node node) {
		char charAt0 = node.toString().trim().charAt(0);
		try {
			Integer.parseInt(charAt0+"");
		}catch (Exception e) {
			return true;
		}
		return false;
	}

	private static boolean obtemDadosDoPac(int i, int no, Node node) throws IOException {				
		if (i == no) {// 71 89
			Idade = node.toString().trim();
			//System.out.println(ln + " " + Idade);
		}
		if (i == (no + 2)) {// 73 91
			Item = node.toString().trim();
			//System.out.println(ln + " " + Item);
		}
		if (i == (no + 4)) {// 75 93
			Qtde = node.toString().trim();
			//System.out.println(ln + " " + Qtde);
		}
		if (i == (no + 6)) {// 77 95
			Realizacao = node.toString().trim();
			//System.out.println(ln + " " + Realizacao);
		}
		if (i == (no + 8)) {// 79 97
			NumAdminssao = node.toString().trim();
			//System.out.println(ln + " " + NumAdminssao);
		}
		if (i == (no + 10)) {// 81 99
			TpoAdmissao = node.toString().replaceAll("&nbsp;", "-").trim();
			//System.out.println(ln + " " + TpoAdmissao);
		}
		if (i == (no + 12)) {// 83 101
			Responsavel = node.toString().trim();
			//System.out.println(i + " " + ln + " " + Responsavel);
			comecouProcuraPeloComplemento = true;
		}		
		if(proximoHeOComplDeVerdade ) {
			Complemento = node.toString().trim();
			//System.out.println(i + " " + ln + " " + Complemento);
			noDeDecisaoDeNovoPac = i + 2;
			proximoHeOComplDeVerdade = false;
			comecouProcuraPeloComplemento = false;
			return true;			
		}
		if (node.toString().trim().equals("<b>Complemento:&nbsp;</b>")) {// 87 105			
			proximoHeOComplDeVerdade = true;
			//System.out.println("proximoHeOComplDeVerdade"+proximoHeOComplDeVerdade);
		}
		if (node.toString().trim().equals("REG0010R")) {// 87 105
			if(comecouProcuraPeloComplemento == true) { 
				detectadaMudancaDePaginaDuranteProcuraDoComplemento = true;
				//System.out.println("detectadaMudancaDePaginaDuranteProcuraDoComplemento "+detectadaMudancaDePaginaDuranteProcuraDoComplemento);
			}	
		}
		return false;
	}

	private static boolean isItAIntegerEMenorQue150(String s) {					
		int n;
		try {
			n = Integer.parseInt(s);
		} catch (NumberFormatException e) {
			return false;
		} catch (NullPointerException e) {
			return false;
		}
		//ser int nao he suficiente, tem pront que nao tem letra
		//entao se for menor que 159 a gente considera uma idade
		//se for maior ai he num de pront sem letra
		if(n > 150) {
			return false;
		}else {
			return true;
		}
	}

	private static void escreveLinha(int ln, int pac, String dtaIni, String dtaFim, String emissao, String PacPront,
			String pacNome, String Idade, String Item, String Qtde, String Realizacao, String NumAdminssao,
			String TpoAdmissao, String Responsavel, String Complemento) throws IOException {

		String strLinha = ln + ";" + pac + ";" + dtaIni + ";" + dtaFim + ";" + emissao + ";" + PacPront + ";" + pacNome
				+ ";" + Idade + ";" + Item + ";" + Qtde + ";" + Realizacao + ";" + NumAdminssao + ";" + TpoAdmissao
				+ ";" + Responsavel + ";" + Complemento + "\n";

		//System.out.printf(strLinha);
		fw.write(strLinha);
	}

	public static String getNomeDasColunas() {
		return ("ln;pac;dtaIni;dtaFim;emissao;PacPront;pacNome;Idade;Item;Qtde;Realizacao;NumAdminssao;TpoAdmissao;Responsavel;Complemento");
	}
}
