package crossix.sort;

import static java.util.Map.entry;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map.Entry;
import java.util.PriorityQueue;

/**
 * @author ogoldenberg
 * @date July 11, 2020
 * @description Big File Sort Util Class contains the implementation of merge Sort Algorithm Logic  
 */
public class BigFileSortUtil {
	
	/**
	 * @description Big File Sort Util Class contains the implementation of merge Sort Algorithm Logic 
	 * @param:  Path input, 
	 * @param:  int maxLinesInMemory
	 * @param:  int sortColNumber
	 * 
	 */
	public boolean sortBigFile(Path input, int maxLinesInMemory, int sortColNumber) throws IOException {
		
		Comparator<String> comparator = (firstLine, secondLine) -> firstLine.split(FileConstants.CSV_DELIMITER)[sortColNumber].compareTo(secondLine.split(FileConstants.CSV_DELIMITER)[sortColNumber]);
		System.out.println(String.format("Spliting %s to sorted files of %s lines in each file", input, maxLinesInMemory));
		List<Path> sortedFiles = splitToSortedFiles(input, maxLinesInMemory, comparator);
		boolean res = mergeSortedFile(sortedFiles, comparator);
		if (res =Boolean.TRUE )
			System.out.println(String.format("Finished Big Sort and Merge successfully. "));
		return res;
	}

	/**
	 * @description splitToSortedFiles 
	 * @param:  Path input, 
	 * @param   int maxLines
	 * @param   Comparator<String> comparator
	 * 
	 */
	private List<Path> splitToSortedFiles(Path input, int maxLines, Comparator<String> comparator) throws IOException {
		List<Path> result = new ArrayList<Path>();
		List<String> lines = new ArrayList<String>();
		Path tempPath = Paths.get(FileConstants.SORTER_OUTPUT_FOLDER + FileConstants.SORTER_TMP_FOLDER);
		Files.createDirectories(tempPath);
		try (BufferedReader reader = Files.newBufferedReader(input)) {
			for(String line; (line = reader.readLine()) != null;) {
				lines.add(line);
				if(lines.size() >= maxLines) {
					result.add(sortAndWriteToTempFiles(tempPath, lines, comparator));
					lines.clear();
				}
			}
		}
		if(!lines.isEmpty()) 
			result.add(sortAndWriteToTempFiles(tempPath, lines, comparator));
		return result;
	}

	private Path sortAndWriteToTempFiles(Path tempPath, List<String> lines, Comparator<String> comparator) {
		Collections.sort(lines, comparator);
		Path tmpFile = null;
		try {
			tmpFile = Files.createTempFile(tempPath, "tempfile", ".txt");
			tmpFile.toFile().deleteOnExit();
			try (BufferedWriter writer = Files.newBufferedWriter(tmpFile)) {
				for(String line : lines) {
					writer.write(line);
					writer.newLine();
				}
			}
		} catch(IOException ex) {
			throw new RuntimeException("sortAndWriteToTempFiles Error! in file (path):" + tmpFile, ex);
		}
		return tmpFile;
	}
	
	private boolean mergeSortedFile(List<Path> tempFiles, Comparator<String> comparator) throws IOException {
		boolean res =Boolean.FALSE;
		String strOutpuFilepath=FileConstants.SORTER_OUTPUT_FOLDER +FileConstants.SORTER_OUTPUT_FILENAME ; 
		System.out.println(String.format("Merging %s sorted files into %s", tempFiles.size(),  Paths.get(strOutpuFilepath)));

		try {
		List<Entry<String, BufferedReader>> currentFiles = new ArrayList<Entry<String, BufferedReader>>();
		for(Path tmpFile : tempFiles) {
			BufferedReader reader = Files.newBufferedReader(tmpFile);
			currentFiles.add(entry(reader.readLine(), reader));
		}
		Comparator<Entry<String, BufferedReader>> cmp = (e1, e2) -> comparator.compare(e1.getKey(), e2.getKey());
		//listDevs.sort((Developer o1, Developer o2)->o1.getId()-o2.getId());
		var priorityQueue = new PriorityQueue<>(tempFiles.size(), cmp);
		priorityQueue.addAll(currentFiles);
		try (BufferedWriter bufferWriter = Files.newBufferedWriter(Paths.get(strOutpuFilepath))) {
			while(!priorityQueue.isEmpty()) {
				Entry<String, BufferedReader> entry = priorityQueue.poll();
				bufferWriter.write(entry.getKey());
				bufferWriter.newLine();
				BufferedReader reader = entry.getValue();
				String line = reader.readLine();
				if(line != null) priorityQueue.add(entry(line, reader));
				else reader.close();
			}
			res=Boolean.TRUE;
		}
		}catch(IOException ioex) {
			System.err.println("mergeSortedFile Error exeption:" + ioex);
		}
		return res;
		
	}

}
