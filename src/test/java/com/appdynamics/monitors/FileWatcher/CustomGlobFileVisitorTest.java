package com.appdynamics.monitors.FileWatcher;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Assert;
import org.junit.Test;

import com.appdynamics.extensions.conf.MonitorConfiguration;
import com.appdynamics.extensions.filewatcher.FileMetric;
import com.appdynamics.extensions.filewatcher.FileWatcherMonitor;
import com.appdynamics.extensions.filewatcher.config.Configuration;
import com.appdynamics.extensions.filewatcher.config.FileToProcess;
import com.appdynamics.extensions.filewatcher.pathmatcher.GlobPathMatcher;
import com.appdynamics.extensions.filewatcher.pathmatcher.factory.PathMatcherFactory;
import com.appdynamics.extensions.filewatcher.pathmatcher.factory.PathMatcherFactory.PathMatcherTypes;
import com.appdynamics.extensions.filewatcher.pathmatcher.helpers.DisplayNameHelper;
import com.appdynamics.extensions.filewatcher.pathmatcher.visitors.CustomGlobFileVisitor;
import com.appdynamics.extensions.util.MetricWriteHelperFactory;

public class CustomGlobFileVisitorTest {

	private CustomGlobFileVisitor globPathFileVisitor;
	

	@Test
	public void test1AllFilesAndDirectoriesRecursivelySuccess() throws Exception{
		FileToProcess f = new FileToProcess();
		f.setDisplayName("test1");
		f.setPath("/A/D/**");
		f.setIgnoreHiddenFiles(false);
		f.setIsDirectoryDetailsRequired(true);
		Configuration conf = new Configuration("Custom Metrics|FileWatcher|", new Runnable(){public void run(){}},MetricWriteHelperFactory.create(new FileWatcherMonitor()));
		
		Path a = Paths.get("/A/D/a.zip");
		Path b = Paths.get("/A/D/b.zip");
		Path c = Paths.get("/A/D/dir");
		List<Path> paths = Arrays.asList(a,b,c);
		GlobPathMatcher matcher = (GlobPathMatcher) PathMatcherFactory.getPathMatcher(PathMatcherTypes.GLOB, f, conf);
		Map<String,FileMetric> map = new HashMap<String, FileMetric>();
		globPathFileVisitor = new CustomGlobFileVisitor(f, matcher, map, "/A/D/");
		for(Path p : paths){
			if(p.toString().equals("/Users/Deepak/dir")){
				globPathFileVisitor.preVisitDirectory(p, null);
			}
			else{
				globPathFileVisitor.visitFile(p, null);
			}
		}
		for(Path p : paths){
			Assert.assertTrue(map.containsKey(DisplayNameHelper.getFormattedDisplayName(f.getDisplayName(), p, "/A/D/")));
		}
	}

	@Test
	public void test2CheckVisitfilesAndDirectorySuccessNoMatch() throws Exception{
		FileToProcess f = new FileToProcess();
		f.setDisplayName("test1");
		f.setPath("/A/D/**");
		f.setIgnoreHiddenFiles(false);
		f.setIsDirectoryDetailsRequired(true);
		Configuration conf = new Configuration("Custom Metrics|FileWatcher|", new Runnable(){public void run(){}},MetricWriteHelperFactory.create(new FileWatcherMonitor()));
;
		Path a = Paths.get("/A/Deepak66/a.zip");
		Path b = Paths.get("/D/Deepak77/b.zip");
		Path c = Paths.get("/A/Deepak88/dir");
		List<Path> paths = Arrays.asList(a,b,c);
		GlobPathMatcher matcher = (GlobPathMatcher) PathMatcherFactory.getPathMatcher(PathMatcherTypes.GLOB, f, conf);
		Map<String,FileMetric> map = new HashMap<String, FileMetric>();
		globPathFileVisitor = new CustomGlobFileVisitor(f, matcher, map, "/A/");
		for(Path p : paths){
			if(p.toString().equals("/Users/Deepak88/dir")){
				globPathFileVisitor.preVisitDirectory(p, null);
			}
			else{
				globPathFileVisitor.visitFile(p, null);
			}
		}
		for(Path p : paths){
			Assert.assertTrue(!map.containsKey(DisplayNameHelper.getFormattedDisplayName(f.getDisplayName(), p, "/A/")));
		}
	}
	
	@Test
	public void test3CheckVisitfilesSingleFileNameSuccess() throws Exception{
		FileToProcess f = new FileToProcess();
		f.setDisplayName("test1");
		f.setPath("/A/D/ABC.XYZ");
		f.setIgnoreHiddenFiles(false);
		f.setIsDirectoryDetailsRequired(true);
		Configuration conf = new Configuration("Custom Metrics|FileWatcher|", new Runnable(){public void run(){}},MetricWriteHelperFactory.create(new FileWatcherMonitor()));
;
		Path a = Paths.get("/A/D/ABC.XYZ");
		GlobPathMatcher matcher = (GlobPathMatcher) PathMatcherFactory.getPathMatcher(PathMatcherTypes.GLOB, f, conf);
		Map<String,FileMetric> map = new HashMap<String, FileMetric>();
		globPathFileVisitor = new CustomGlobFileVisitor(f, matcher, map, "/A/D/");

		globPathFileVisitor.visitFile(a, null);
		Assert.assertTrue(map.containsKey(DisplayNameHelper.getFormattedDisplayName(f.getDisplayName(), a, "/A/D/")));
		Assert.assertEquals(map.keySet().toArray()[0], "test1|ABC.XYZ");
	}

	@Test
	public void test4AllFilesAndDirectoriesNonRecursivelySuccess() throws Exception{
		FileToProcess f = new FileToProcess();
		f.setDisplayName("test1");
		f.setPath("/A/D/*");
		f.setIgnoreHiddenFiles(false);
		f.setIsDirectoryDetailsRequired(true);
		Configuration conf = new Configuration("Custom Metrics|FileWatcher|", new Runnable(){public void run(){}},MetricWriteHelperFactory.create(new FileWatcherMonitor()));
;
		Path a = Paths.get("/A/D/ABC.XYZ");
		Path b = Paths.get("/A/D/A");
		Path c  = Paths.get("/A/D/F/G");
		List<Path> paths = Arrays.asList(a,b,c);
		GlobPathMatcher matcher = (GlobPathMatcher) PathMatcherFactory.getPathMatcher(PathMatcherTypes.GLOB, f, conf);
		Map<String,FileMetric> map = new HashMap<String, FileMetric>();
		globPathFileVisitor = new CustomGlobFileVisitor(f, matcher, map, "/A/D/");
		for(Path p : paths){
			if(p.toString().equals("/A/D/A") || p.toString().equals("/A/D/F/G")){
				globPathFileVisitor.preVisitDirectory(p, null);
			}
			else{
				globPathFileVisitor.visitFile(p, null);
			}
		}
		Assert.assertTrue(map.containsKey(DisplayNameHelper.getFormattedDisplayName(f.getDisplayName(), a, "/A/D/")));
		Assert.assertTrue(map.containsKey(DisplayNameHelper.getFormattedDisplayName(f.getDisplayName(), b, "/A/D/")));
		Assert.assertTrue(!map.containsKey(DisplayNameHelper.getFormattedDisplayName(f.getDisplayName(), c, "/A/D/")));
	}
	
	@Test
	public void test5OnlyDirectoriesNoFilesSuccess() throws Exception{
		FileToProcess f = new FileToProcess();
		f.setDisplayName("test1");
		f.setPath("/A/D/*");
		f.setIgnoreHiddenFiles(false);
		f.setIsDirectoryDetailsRequired(false);
		Configuration conf = new Configuration("Custom Metrics|FileWatcher|", new Runnable(){public void run(){}},MetricWriteHelperFactory.create(new FileWatcherMonitor()));
;
		Path a = Paths.get("/A/D/ABC.XYZ");
		Path b = Paths.get("/A/D/A");
		Path c  = Paths.get("/A/D/F/G");
		List<Path> paths = Arrays.asList(a,b,c);
		GlobPathMatcher matcher = (GlobPathMatcher) PathMatcherFactory.getPathMatcher(PathMatcherTypes.GLOB, f, conf);
		Map<String,FileMetric> map = new HashMap<String, FileMetric>();
		globPathFileVisitor = new CustomGlobFileVisitor(f, matcher, map, "/A/D/");
		for(Path p : paths){
			if(p.toString().equals("/A/D/A") || p.toString().equals("/A/D/F/G")){
				globPathFileVisitor.preVisitDirectory(p, null);
			}
			else{
				globPathFileVisitor.visitFile(p, null);
			}
		}
		Assert.assertTrue(!map.containsKey(DisplayNameHelper.getFormattedDisplayName(f.getDisplayName(), a, "/A/D/")));
		Assert.assertTrue(map.containsKey(DisplayNameHelper.getFormattedDisplayName(f.getDisplayName(), b, "/A/D/")));
		Assert.assertTrue(!map.containsKey(DisplayNameHelper.getFormattedDisplayName(f.getDisplayName(), c, "/A/D/")));
	}
	
	@Test
	public void test6OnlyFilesNoDirectoriesSuccess() throws Exception{
		FileToProcess f = new FileToProcess();
		f.setDisplayName("test1");
		f.setPath("/A/D/*.*");
		f.setIgnoreHiddenFiles(false);
		f.setIsDirectoryDetailsRequired(true);
		Configuration conf = new Configuration("Custom Metrics|FileWatcher|", new Runnable(){public void run(){}},MetricWriteHelperFactory.create(new FileWatcherMonitor()));
;
		Path a = Paths.get("/A/D/ABC.XYZ");
		Path b = Paths.get("/A/D/A");
		Path c  = Paths.get("/A/D/F/G");
		List<Path> paths = Arrays.asList(a,b,c);
		GlobPathMatcher matcher = (GlobPathMatcher) PathMatcherFactory.getPathMatcher(PathMatcherTypes.GLOB, f, conf);
		Map<String,FileMetric> map = new HashMap<String, FileMetric>();
		globPathFileVisitor = new CustomGlobFileVisitor(f, matcher, map, "/A/D/");
		for(Path p : paths){
			if(p.toString().equals("/A/D/A") || p.toString().equals("/A/D/F/G")){
				globPathFileVisitor.preVisitDirectory(p, null);
			}
			else{
				globPathFileVisitor.visitFile(p, null);
			}
		}
		Assert.assertTrue(map.containsKey(DisplayNameHelper.getFormattedDisplayName(f.getDisplayName(), a, "/A/D/")));
		Assert.assertTrue(!map.containsKey(DisplayNameHelper.getFormattedDisplayName(f.getDisplayName(), b, "/A/D/")));
		Assert.assertTrue(!map.containsKey(DisplayNameHelper.getFormattedDisplayName(f.getDisplayName(), c, "/A/D/")));
	}
	
	@Test
	public void test7OnlyDirectorySingleSuccess() throws Exception{
		FileToProcess f = new FileToProcess();
		f.setDisplayName("test1");
		f.setPath("/A/D/C");
		f.setIgnoreHiddenFiles(false);
		f.setIsDirectoryDetailsRequired(true);
		Configuration conf = new Configuration("Custom Metrics|FileWatcher|", new Runnable(){public void run(){}},MetricWriteHelperFactory.create(new FileWatcherMonitor()));
;
		Path a = Paths.get("/A/D/C/ABC.XYZ");
		Path b = Paths.get("/A/D/C");
		List<Path> paths = Arrays.asList(a,b);
		GlobPathMatcher matcher = (GlobPathMatcher) PathMatcherFactory.getPathMatcher(PathMatcherTypes.GLOB, f, conf);
		Map<String,FileMetric> map = new HashMap<String, FileMetric>();
		globPathFileVisitor = new CustomGlobFileVisitor(f, matcher, map, "/A/D/C/");
		for(Path p : paths){
			if(p.toString().equals("/A/D/C")){
				globPathFileVisitor.preVisitDirectory(p, null);
			}
			else{
				globPathFileVisitor.visitFile(p, null);
			}
		}
		Assert.assertTrue(!map.containsKey(DisplayNameHelper.getFormattedDisplayName(f.getDisplayName(), a, "/A/D/C/")));
		Assert.assertTrue(map.containsKey(DisplayNameHelper.getFormattedDisplayName(f.getDisplayName(), b, "/A/D/C/")));
	}
	
	@Test
	public void test8MiddleWildcardDoubleOnlyFilesSuccess() throws Exception{
		FileToProcess f = new FileToProcess();
		f.setDisplayName("test1");
		f.setPath("/A/D/C/**/*.zip");
		f.setIgnoreHiddenFiles(false);
		f.setIsDirectoryDetailsRequired(true);
		Configuration conf = new Configuration("Custom Metrics|FileWatcher|", new Runnable(){public void run(){}},MetricWriteHelperFactory.create(new FileWatcherMonitor()));
;
		Path a = Paths.get("/A/D/C/ABC.zip");
		Path b = Paths.get("/A/D/C/D/E/123.zip");
		Path c = Paths.get("/A/D/55.zip");
		Path d = Paths.get("/A/D/F");
		List<Path> paths = Arrays.asList(a,b,c,d);
		GlobPathMatcher matcher = (GlobPathMatcher) PathMatcherFactory.getPathMatcher(PathMatcherTypes.GLOB, f, conf);
		Map<String,FileMetric> map = new HashMap<String, FileMetric>();
		globPathFileVisitor = new CustomGlobFileVisitor(f, matcher, map, "/A/D/C/");
		for(Path p : paths){
			if(p.toString().equals("/A/D/C/D")){
				globPathFileVisitor.preVisitDirectory(p, null);
			}
			else{
				globPathFileVisitor.visitFile(p, null);
			}
		}
		Assert.assertTrue(!map.containsKey(DisplayNameHelper.getFormattedDisplayName(f.getDisplayName(), a, "/A/D/C/")));
		Assert.assertTrue(map.containsKey(DisplayNameHelper.getFormattedDisplayName(f.getDisplayName(), b, "/A/D/C/")));
		Assert.assertTrue(!map.containsKey(DisplayNameHelper.getFormattedDisplayName(f.getDisplayName(), c, "/A/D/C/")));
		Assert.assertTrue(!map.containsKey(DisplayNameHelper.getFormattedDisplayName(f.getDisplayName(), d, "/A/D/C/")));
	}
	
	@Test
	public void test9MiddleWildcardDoubleBothDirectoriesAndFilesSuccess() throws Exception{
		FileToProcess f = new FileToProcess();
		f.setDisplayName("test1");
		f.setPath("/A/D/C/**");
		f.setIgnoreHiddenFiles(false);
		f.setIsDirectoryDetailsRequired(true);
		Configuration conf = new Configuration("Custom Metrics|FileWatcher|", new Runnable(){public void run(){}},MetricWriteHelperFactory.create(new FileWatcherMonitor()));
;
		Path a = Paths.get("/A/D/C/ABC.zip");
		Path b = Paths.get("/A/D/C/D/E/123.zip");
		Path c = Paths.get("/A/D/55.zip");
		Path d = Paths.get("/A/D/C/F");
		List<Path> paths = Arrays.asList(a,b,c,d);
		GlobPathMatcher matcher = (GlobPathMatcher) PathMatcherFactory.getPathMatcher(PathMatcherTypes.GLOB, f, conf);
		Map<String,FileMetric> map = new HashMap<String, FileMetric>();
		globPathFileVisitor = new CustomGlobFileVisitor(f, matcher, map, "/A/D/C/");
		for(Path p : paths){
			if(p.toString().equals("/A/D/C/F")){
				globPathFileVisitor.preVisitDirectory(p, null);
			}
			else{
				globPathFileVisitor.visitFile(p, null);
			}
		}
		Assert.assertTrue(map.containsKey(DisplayNameHelper.getFormattedDisplayName(f.getDisplayName(), a, "/A/D/C/")));
		Assert.assertTrue(map.containsKey(DisplayNameHelper.getFormattedDisplayName(f.getDisplayName(), b, "/A/D/C/")));
		Assert.assertTrue(!map.containsKey(DisplayNameHelper.getFormattedDisplayName(f.getDisplayName(), c, "/A/D/C/")));
		Assert.assertTrue(map.containsKey(DisplayNameHelper.getFormattedDisplayName(f.getDisplayName(), d, "/A/D/C/")));
	}
	
	@Test
	public void test10MiddleWildcardDSingleBothDirectoriesAndFilesSuccess() throws Exception{
		FileToProcess f = new FileToProcess();
		f.setDisplayName("test1");
		f.setPath("/A/D/C/*");
		f.setIgnoreHiddenFiles(false);
		f.setIsDirectoryDetailsRequired(true);
		Configuration conf = new Configuration("Custom Metrics|FileWatcher|", new Runnable(){public void run(){}},MetricWriteHelperFactory.create(new FileWatcherMonitor()));
;
		Path a = Paths.get("/A/D/C/ABC.zip");
		Path b = Paths.get("/A/D/C/D/E/123.zip");
		Path c = Paths.get("/A/D/55.zip");
		Path d = Paths.get("/A/D/C/F");
		List<Path> paths = Arrays.asList(a,b,c,d);
		GlobPathMatcher matcher = (GlobPathMatcher) PathMatcherFactory.getPathMatcher(PathMatcherTypes.GLOB, f, conf);
		Map<String,FileMetric> map = new HashMap<String, FileMetric>();
		globPathFileVisitor = new CustomGlobFileVisitor(f, matcher, map, "/A/D/C/");
		for(Path p : paths){
			if(p.toString().equals("/A/D/C/F")){
				globPathFileVisitor.preVisitDirectory(p, null);
			}
			else{
				globPathFileVisitor.visitFile(p, null);
			}
		}
		Assert.assertTrue(map.containsKey(DisplayNameHelper.getFormattedDisplayName(f.getDisplayName(), a, "/A/D/C/")));
		Assert.assertTrue(!map.containsKey(DisplayNameHelper.getFormattedDisplayName(f.getDisplayName(), b, "/A/D/C/")));
		Assert.assertTrue(!map.containsKey(DisplayNameHelper.getFormattedDisplayName(f.getDisplayName(), c, "/A/D/C/")));
		Assert.assertTrue(map.containsKey(DisplayNameHelper.getFormattedDisplayName(f.getDisplayName(), a, "/A/D/C/")));
	}
	
	@Test
	public void test11MiddleWildcardDSingleOnlyFilesSuccess() throws Exception{
		FileToProcess f = new FileToProcess();
		f.setDisplayName("test1");
		f.setPath("/A/D/C/*.zip");
		f.setIgnoreHiddenFiles(false);
		f.setIsDirectoryDetailsRequired(true);
		Configuration conf = new Configuration("Custom Metrics|FileWatcher|", new Runnable(){public void run(){}},MetricWriteHelperFactory.create(new FileWatcherMonitor()));
;
		Path a = Paths.get("/A/D/C/ABC.zip");
		Path b = Paths.get("/A/D/C/D/E/123.zip");
		Path c = Paths.get("/A/D/55.zip");
		Path d = Paths.get("/A/D/C/F");
		List<Path> paths = Arrays.asList(a,b,c,d);
		GlobPathMatcher matcher = (GlobPathMatcher) PathMatcherFactory.getPathMatcher(PathMatcherTypes.GLOB, f, conf);
		Map<String,FileMetric> map = new HashMap<String, FileMetric>();
		globPathFileVisitor = new CustomGlobFileVisitor(f, matcher, map, "/A/D/C/");
		for(Path p : paths){
			if(p.toString().equals("/A/D/C/F")){
				globPathFileVisitor.preVisitDirectory(p, null);
			}
			else{
				globPathFileVisitor.visitFile(p, null);
			}
		}
		Assert.assertTrue(map.containsKey(DisplayNameHelper.getFormattedDisplayName(f.getDisplayName(), a, "/A/D/C/")));
		Assert.assertTrue(!map.containsKey(DisplayNameHelper.getFormattedDisplayName(f.getDisplayName(), b, "/A/D/C/")));
		Assert.assertTrue(!map.containsKey(DisplayNameHelper.getFormattedDisplayName(f.getDisplayName(), c, "/A/D/C/")));
		Assert.assertTrue(!map.containsKey(DisplayNameHelper.getFormattedDisplayName(f.getDisplayName(), d, "/A/D/C/")));
	}
	
	@Test
	public void test11StartingAndEndingWildcardDSingleOnlyFilesSuccess() throws Exception{
		FileToProcess f = new FileToProcess();
		f.setDisplayName("test1");
		f.setPath("*/A/D/C/*.zip");
		f.setIgnoreHiddenFiles(false);
		f.setIsDirectoryDetailsRequired(true);
		Configuration conf = new Configuration("Custom Metrics|FileWatcher|", new Runnable(){public void run(){}},MetricWriteHelperFactory.create(new FileWatcherMonitor()));
;
		Path a = Paths.get("/A/D/C/ABC.zip");
		Path b = Paths.get("/A/D/C/D/E/123.zip");
		Path c = Paths.get("/A/D/55.zip");
		Path d = Paths.get("/A/D/C/F");
		List<Path> paths = Arrays.asList(a,b,c,d);
		GlobPathMatcher matcher = (GlobPathMatcher) PathMatcherFactory.getPathMatcher(PathMatcherTypes.GLOB, f, conf);
		Map<String,FileMetric> map = new HashMap<String, FileMetric>();
		globPathFileVisitor = new CustomGlobFileVisitor(f, matcher, map, "/");
		for(Path p : paths){
			if(p.toString().equals("/A/D/C/F")){
				globPathFileVisitor.preVisitDirectory(p, null);
			}
			else{
				globPathFileVisitor.visitFile(p, null);
			}
		}
		Assert.assertTrue(map.containsKey(DisplayNameHelper.getFormattedDisplayName(f.getDisplayName(), a, "/A/D/C/F/")));
		Assert.assertTrue(!map.containsKey(DisplayNameHelper.getFormattedDisplayName(f.getDisplayName(), b, "/A/D/C/F/")));
		Assert.assertTrue(!map.containsKey(DisplayNameHelper.getFormattedDisplayName(f.getDisplayName(), c, "/A/D/C/F/")));
		Assert.assertTrue(!map.containsKey(DisplayNameHelper.getFormattedDisplayName(f.getDisplayName(), d, "/A/D/C/F/")));
	}
	
	@Test
	public void test12StartingAndEndingWildcardDSingleOnlyDirecotriesSuccess() throws Exception{
		FileToProcess f = new FileToProcess();
		f.setDisplayName("test1");
		f.setPath("*/A/D/C/*");
		f.setIgnoreHiddenFiles(false);
		f.setIsDirectoryDetailsRequired(true);
		Configuration conf = new Configuration("Custom Metrics|FileWatcher|", new Runnable(){public void run(){}},MetricWriteHelperFactory.create(new FileWatcherMonitor()));
;
		Path a = Paths.get("/A/D/C/ABC.zip");
		Path b = Paths.get("/A/D/C/D/E/123.zip");
		Path c = Paths.get("/A/D/55.zip");
		Path d = Paths.get("/A/D/C/F");
		List<Path> paths = Arrays.asList(a,b,c,d);
		GlobPathMatcher matcher = (GlobPathMatcher) PathMatcherFactory.getPathMatcher(PathMatcherTypes.GLOB, f, conf);
		Map<String,FileMetric> map = new HashMap<String, FileMetric>();
		globPathFileVisitor = new CustomGlobFileVisitor(f, matcher, map, "/");
		for(Path p : paths){
			if(p.toString().equals("/A/D/C/F")){
				globPathFileVisitor.preVisitDirectory(p, null);
			}
			else{
				globPathFileVisitor.visitFile(p, null);
			}
		}
		Assert.assertTrue(map.containsKey(DisplayNameHelper.getFormattedDisplayName(f.getDisplayName(), a, "/A/D/C/F/")));
		Assert.assertTrue(!map.containsKey(DisplayNameHelper.getFormattedDisplayName(f.getDisplayName(), b, "/A/D/C/F/")));
		Assert.assertTrue(!map.containsKey(DisplayNameHelper.getFormattedDisplayName(f.getDisplayName(), c, "/A/D/C/F/")));
		Assert.assertTrue(!map.containsKey(DisplayNameHelper.getFormattedDisplayName(f.getDisplayName(), d, "/A/D/C/F/")));
	}
	
	@Test
	public void test13StartingAndEndingDoubleWildcardDSingleOnlyDirecotriesSuccess() throws Exception{
		FileToProcess f = new FileToProcess();
		f.setDisplayName("test1");
		f.setPath("**/A/D/C/*");
		f.setIgnoreHiddenFiles(false);
		f.setIsDirectoryDetailsRequired(true);
		Configuration conf = new Configuration("Custom Metrics|FileWatcher|", new Runnable(){public void run(){}},MetricWriteHelperFactory.create(new FileWatcherMonitor()));
;
		Path a = Paths.get("/A/D/C/ABC.zip");
		Path b = Paths.get("/A/D/C/D/E/123.zip");
		Path c = Paths.get("/A/D/55.zip");
		Path d = Paths.get("/A/D/C/F");
		List<Path> paths = Arrays.asList(a,b,c,d);
		GlobPathMatcher matcher = (GlobPathMatcher) PathMatcherFactory.getPathMatcher(PathMatcherTypes.GLOB, f, conf);
		Map<String,FileMetric> map = new HashMap<String, FileMetric>();
		globPathFileVisitor = new CustomGlobFileVisitor(f, matcher, map, "/");
		for(Path p : paths){
			if(p.toString().equals("/A/D/C/F/")){
				globPathFileVisitor.preVisitDirectory(p, null);
			}
			else{
				globPathFileVisitor.visitFile(p, null);
			}
		}
		Assert.assertTrue(map.containsKey(DisplayNameHelper.getFormattedDisplayName(f.getDisplayName(), a, "/A/D/C/F/")));
		Assert.assertTrue(!map.containsKey(DisplayNameHelper.getFormattedDisplayName(f.getDisplayName(), b, "/A/D/C/F/")));
		Assert.assertTrue(!map.containsKey(DisplayNameHelper.getFormattedDisplayName(f.getDisplayName(), c, "/A/D/C/F/")));
		Assert.assertTrue(!map.containsKey(DisplayNameHelper.getFormattedDisplayName(f.getDisplayName(), d, "/A/D/C/F/")));
	}
	
	@Test
	public void test14StartingAndEndingDoubleWildcardDSingleBothDirecotriesAndFilesSuccess() throws Exception{
		FileToProcess f = new FileToProcess();
		f.setDisplayName("test1");
		f.setPath("**/A/D/C/**");
		f.setIgnoreHiddenFiles(false);
		f.setIsDirectoryDetailsRequired(true);
		Configuration conf = new Configuration("Custom Metrics|FileWatcher|", new Runnable(){public void run(){}},MetricWriteHelperFactory.create(new FileWatcherMonitor()));
;
		Path a = Paths.get("/A/D/C/ABC.zip");
		Path b = Paths.get("/A/D/C/D/E/123.zip");
		Path c = Paths.get("/A/D/55.zip");
		Path d = Paths.get("/A/D/C/F");
		List<Path> paths = Arrays.asList(a,b,c,d);
		GlobPathMatcher matcher = (GlobPathMatcher) PathMatcherFactory.getPathMatcher(PathMatcherTypes.GLOB, f, conf);
		Map<String,FileMetric> map = new HashMap<String, FileMetric>();
		globPathFileVisitor = new CustomGlobFileVisitor(f, matcher, map, "/");
		for(Path p : paths){
			if(p.toString().equals("/A/D/C/F/")){
				globPathFileVisitor.preVisitDirectory(p, null);
			}
			else{
				globPathFileVisitor.visitFile(p, null);
			}
		}
		Assert.assertTrue(map.containsKey(DisplayNameHelper.getFormattedDisplayName(f.getDisplayName(), a, "/")));
		Assert.assertTrue(map.containsKey(DisplayNameHelper.getFormattedDisplayName(f.getDisplayName(), b, "/")));
		Assert.assertTrue(!map.containsKey(DisplayNameHelper.getFormattedDisplayName(f.getDisplayName(), c, "/")));
		Assert.assertTrue(map.containsKey(DisplayNameHelper.getFormattedDisplayName(f.getDisplayName(), d, "/")));
	}
	
	@Test
	public void test15StartingAndEndingAndMiddleSingleWildcardDSingleBothDirecotriesAndFilesSuccess() throws Exception{
		FileToProcess f = new FileToProcess();
		f.setDisplayName("test1");
		f.setPath("**/A/*/C/**");
		f.setIgnoreHiddenFiles(false);
		f.setIsDirectoryDetailsRequired(true);
		Configuration conf = new Configuration("Custom Metrics|FileWatcher|", new Runnable(){public void run(){}},MetricWriteHelperFactory.create(new FileWatcherMonitor()));
;
		Path a = Paths.get("/A/D/C/ABC.zip");
		Path b = Paths.get("/A/D/C/D/E/123.zip");
		Path c = Paths.get("/A/D/55.zip");
		Path d = Paths.get("/A/D/C/F");
		List<Path> paths = Arrays.asList(a,b,c,d);
		GlobPathMatcher matcher = (GlobPathMatcher) PathMatcherFactory.getPathMatcher(PathMatcherTypes.GLOB, f, conf);
		Map<String,FileMetric> map = new HashMap<String, FileMetric>();
		globPathFileVisitor = new CustomGlobFileVisitor(f, matcher, map, "/");
		for(Path p : paths){
			if(p.toString().equals("/A/D/C/F")){
				globPathFileVisitor.preVisitDirectory(p, null);
			}
			else{
				globPathFileVisitor.visitFile(p, null);
			}
		}
		Assert.assertTrue(map.containsKey(DisplayNameHelper.getFormattedDisplayName(f.getDisplayName(), a, "/")));
		Assert.assertTrue(map.containsKey(DisplayNameHelper.getFormattedDisplayName(f.getDisplayName(), b, "/")));
		Assert.assertTrue(!map.containsKey(DisplayNameHelper.getFormattedDisplayName(f.getDisplayName(), c, "/")));
		Assert.assertTrue(map.containsKey(DisplayNameHelper.getFormattedDisplayName(f.getDisplayName(), d, "/")));
	}

}
