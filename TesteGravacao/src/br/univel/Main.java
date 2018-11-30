package br.univel;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.SequenceInputStream;
import java.nio.channels.ShutdownChannelGroupException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class Main {

	public static String createby;
	private static long inicioExecucao;
	private static long terminoExecucao;
	private static long resultado;
	static int i;

	public static void main(String[] args) {
		System.out.println("olá");
		
		System.out.println("----------------------------------------------------------");
		System.out.println("                  Teste Sequencial"                        );
		System.out.println("----------------------------------------------------------");
		gravacaoSequencial();
		System.out.println("----------------------------------------------------------");
		System.out.println("                 Teste Rondomico - 4K"                     );
		System.out.println("----------------------------------------------------------");
		gravacaoBlocos();
		System.out.println("----------------------------------------------------------");
		System.out.println("             Teste Randômico de 4K (mult-thread)"          );
		System.out.println("----------------------------------------------------------");
		gravacaoThreads();
		System.out.println("----------------------------------------------------------");
		
		createby = "Dhonatan Wesley";
		System.out.println("by: "+createby);
	}

	// Método de gravação de um arquivo de 500 mb
	private static void gravacaoSequencial() {

		long tamanho = (500000 * 1024); // 500 Mb

		System.out.println("Fazendo Gravação Sequencial - 500 Mb");

		// Inicia Contagem do tempo de gravação
		inicioExecucao = System.currentTimeMillis();

		// Cria Texto preencher arquivo
		StringBuilder stringBuilder = new StringBuilder();
		for (int i = 0; i < tamanho; i++) {
			stringBuilder.append("0");
		}

		// cria arquivo, popula com o texto gerado acima e grava no disco
		File arquivo = new File("arquivo.txt");
		try {
			if (arquivo.createNewFile()) {
				FileOutputStream fileOutputStream = new FileOutputStream(arquivo);
				fileOutputStream.write(stringBuilder.toString().getBytes());
				fileOutputStream.flush();
				fileOutputStream.close();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}

		// termina contagem de gravação
		terminoExecucao = System.currentTimeMillis();
		
		resultado = (terminoExecucao - inicioExecucao);
		
		System.out.println("Resultado: " + resultado + " ms");

		System.out.println("----------------------------------------------------------");
		System.out.println("Fazendo Leitura Sequencial");

		// inicia contagem do tempo de leitura
		inicioExecucao = System.currentTimeMillis();

		// Ler arquivo
		try (BufferedInputStream bufferedInputStream = new BufferedInputStream(new FileInputStream(arquivo))) {
			while (bufferedInputStream.read() != -1) {
			}
			bufferedInputStream.close();
		} catch (IOException e) {
			e.printStackTrace();
		}

		// termina a contagem do tempo de leitura
		terminoExecucao = System.currentTimeMillis();

		resultado = (terminoExecucao - inicioExecucao);
		
		System.out.println("Resultado: " + resultado + " ms");
		arquivo.delete();
	}

	// Método de gravação em blocos de 4k
	private static void gravacaoBlocos() {

		long tamanho = (4000); // 4Kb

		System.out.println("Fazendo gravação de blocos de 4Kb");

		// Inicia contagem de gravação
		inicioExecucao = System.currentTimeMillis();

		// Cria Texto para preencher arquivo
		StringBuilder stringBuilder = new StringBuilder();

		for (int i = 0; i < tamanho; i++) {
			stringBuilder.append("A");
		}

		// Cria arquivo e popula com o texto gerado acima

		File arquivo;

		for (int i = 0; i < 64; i++) {

			arquivo = new File("arquivo" + i + ".txt");

			try {
				if (arquivo.createNewFile()) {
					FileOutputStream fileOutputStream = new FileOutputStream(arquivo);
					fileOutputStream.write(stringBuilder.toString().getBytes());
					fileOutputStream.flush();
					fileOutputStream.close();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		// Termina contagem do tempo
		terminoExecucao = System.currentTimeMillis();
		
		resultado = (terminoExecucao - inicioExecucao);
		
		System.out.println("Resultado: " + resultado + " ms");


		System.out.println("----------------------------------------------------------");
		System.out.println("Fazendo leitura de blocos de 4Kb");

		// inicia contagem de leitura
		inicioExecucao = System.currentTimeMillis();

		// Ler arquivo
		for (int i = 0; i < 64; i++) {
			try (BufferedInputStream bufferedInputStream = new BufferedInputStream(
					new FileInputStream("arquivo" + i + ".txt"))) {
				while (bufferedInputStream.read() != -1) {
				}
				bufferedInputStream.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		// temina contagem
		terminoExecucao = System.currentTimeMillis();
		
		resultado = (terminoExecucao - inicioExecucao);
		System.out.println("Resultado: " + resultado + " ms");

	}

	// Método de gravação em blocos de 4k por Threads
	private static void gravacaoThreads() {

		i = 0;

		long tamanho = (4000); // 4Kb

		System.out.println("Fazendo Gravação por Threads");

		// Inicia contagem de gravação
		inicioExecucao = System.currentTimeMillis();

		// Cria Texto para preencher arquivo
		StringBuilder stringBuilder = new StringBuilder();

		for (int i = 0; i < tamanho; i++) {
			stringBuilder.append("0");
		}

		// Cria arquivo e popula com o texto gerado acima

		ExecutorService executorService = Executors.newFixedThreadPool(64);

		for (i = 0; i < 64; i++) {
			executorService.execute(new Runnable() {
				@Override
				public void run() {
					for (int j = 0; j < 64; j++) {
						File arquivo = new File("arquivo" + i + j + ".txt");
						try {
							if (arquivo.createNewFile()) {
								FileOutputStream fileOutputStream = new FileOutputStream(arquivo);
								fileOutputStream.write(stringBuilder.toString().getBytes());
								fileOutputStream.flush();
								fileOutputStream.close();
							}
						} catch (IOException e) {
							e.printStackTrace();
						}
					}
				}
			});
		}
		executorService.shutdown();
		try {
			executorService.awaitTermination(Long.MAX_VALUE, TimeUnit.HOURS);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

		// Termina contagem do tempo
		terminoExecucao = System.currentTimeMillis();
		
		resultado =(terminoExecucao - inicioExecucao);
		
		System.out.println("Resultado: " + resultado + " ms");


		System.out.println("----------------------------------------------------------");
		System.out.println("Leitura por Threads");

		// inicia contagem de leitura
		inicioExecucao = System.currentTimeMillis();
		
		File arquivo2 = new File("arquivo.txt");
		try {
			if (arquivo2.createNewFile()) {
				FileOutputStream fileOutputStream = new FileOutputStream(arquivo2);
				fileOutputStream.write(stringBuilder.toString().getBytes());
				fileOutputStream.flush();
				fileOutputStream.close();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		ExecutorService executorService2 = Executors.newFixedThreadPool(64);

        for (i = 0; i < 64; i++) {
            executorService2.execute(new Runnable() {
                @Override
                public void run() {
                    for (int j = 0; j < (262144 / 64); j++) {
                        try (BufferedInputStream bufferedInputStream = new BufferedInputStream(new FileInputStream(arquivo2))) {
                            while (bufferedInputStream.read() != -1){}
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
            });
        }		
		
		// temina contagem
		terminoExecucao = System.currentTimeMillis();
		
		resultado = (terminoExecucao - inicioExecucao);
		
		System.out.println("Resultado: " + resultado + " ms");

	}
}
