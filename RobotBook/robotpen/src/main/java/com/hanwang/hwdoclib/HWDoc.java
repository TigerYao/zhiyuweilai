// vim:ts=4:sw=4:sts=4:et
package com.hanwang.hwdoclib;

import java.util.ArrayList;

import android.graphics.Rect;
import android.util.Log;

public class HWDoc {
    public static final String LOG_TAG = "HWDoc";

	public static final int HW_RANGE_GB18030 = 0xF;
	public static final int HW_RANGE_DIGIT = 0x100;
	public static final int HW_RANGE_ALPHA = 0x600;
	public static final int HW_RANGE_PUNCT = 0x7800;
	
    private int mDocHandle = 0;
    static {
        System.loadLibrary("hwdoc");
    }

    private static native  int      nativeNewDocEngine();
    private static native  int      nativeSetRange(int handle, int range);
    private static native  boolean  nativeSetChsSentenceOverlap(int handle, boolean bChsSentenceOverlap);
    private static native  boolean  nativeGetChsSentenceOverlap(int handle);
    private static native  boolean  nativeLoadIndexFile(int handle, String idxFileName);
    private static native  boolean  nativeSaveIndexFile(int handle, String idxFileName);
    

    private static native  boolean  nativeAnalysisDocument(int handle, short[] stroke);
    
    

    private static native  int      nativeGetGesture(int handle, short[] trace);


    private static native  boolean  nativeSegmentInputTrace(int handle, short[] trace);
    

    private static native  int[]    nativeGetInputSegmentIndexes(int handle);
    

    private static native  short[]  nativeGetInputSegmentRects(int handle);
    

    private static native  int[]    nativeGetInputSegmentLangs(int handle);
    

    private static native  String   nativeGetInputSegmentText(int handle);
    

    private static native  boolean  nativeDocAddInputTrace(int handle, short[] trace);
    

    private static native  boolean  nativeDocInsertInputTrace(int handle, int offset, short[] trace);
    
//
    private static native  boolean  nativeDocInsertSpace(int handle, int offset);
    
//
    private static native  boolean  nativeDocInsertCarriageReturn(int handle, int offset);
    

    private static native  boolean  nativeDocRemoveWords(int handle, int start, int end);
    
//
    private static native  int[]    nativeSearchKeywordRanges(int handle, String text);   
     
//
    private static native  short[]  nativeSearchKeywordRects(int handle, String text);  
     
//
    private static native  int[]    nativeSearchTraceRanges(int handle, short[] trace);  

    private static native  short[]  nativeSearchTraceRects(int handle, short[] trace);  
     

    private static native  String   nativeGetDocSegmentText(int handle);  
     

    private static native  void     nativeClearDocEngine(int handle);  
     

    private static native  void     nativeDeleteDocEngine(int handle);
    
    
    
    
    
    public class StrokeRange {
        public int startStroke;
        public int startPoint;
        public int endStroke;
        public int endPoint;
        
        public StrokeRange(int startStroke, int startPoint, int endStroke, int endPoint) {
            this.startStroke = startStroke;
            this.startPoint  = startPoint;
            this.endStroke   = endStroke;
            this.endPoint    = endPoint;
        }
    }

    public HWDoc() {
        Log.e(LOG_TAG, "HWDoc constructor.");
        mDocHandle = nativeNewDocEngine();
    }
    
    protected void finalize() {
        nativeDeleteDocEngine(mDocHandle);
    }

    public boolean isValid() {
        return mDocHandle != 0;
    }
	

	public boolean setRange(int range) {
		if (mDocHandle != 0)
        {
            if(nativeSetRange(mDocHandle, range) == 0)
			return true;
		}
		return false;
	}

    public boolean setChsSentenceOverlap(boolean bChsSentenceOverlap) {
        if (mDocHandle != 0) {
            return nativeSetChsSentenceOverlap(mDocHandle, bChsSentenceOverlap);
        }
        return false;
    }
    
    public boolean getChsSentenceOverlap() {
        if (mDocHandle != 0) {
            return nativeGetChsSentenceOverlap(mDocHandle);
        }
        return false;
    }

    public boolean loadIndexFile(String indexFileName) {
        if (mDocHandle != 0) {
            return nativeLoadIndexFile(mDocHandle, indexFileName);
        }
        return false;
    }

    public boolean saveIndexFile(String indexFileName) {
        if (mDocHandle != 0) {
            return nativeSaveIndexFile(mDocHandle, indexFileName);
        }
        return false;
    }

    public void clear() {
        if (mDocHandle != 0) {
            nativeClearDocEngine(mDocHandle);
        }
    }
    
    public int getGestureCtrl(short[] trace) {
        if (mDocHandle != 0) {
            return nativeGetGesture(mDocHandle, trace);
        }
        return 0;
    }
    
    public boolean segmentInputTrace(short[] trace) {
        if (mDocHandle != 0) {
            return nativeSegmentInputTrace(mDocHandle, trace);
        }
        return false;
    }
    
    public int[] getSegmentIndexes() {
        if (mDocHandle != 0) {
            return nativeGetInputSegmentIndexes(mDocHandle);
        }
        return null;
    }

    public ArrayList<Rect> getSegmentRects() {
        if (mDocHandle != 0) {
            short[] segResult = nativeGetInputSegmentRects(mDocHandle);
            return shortsToRects(segResult);
        }
        return null;
    }

    // Chinese: false
    // English: true
    public ArrayList<Boolean> getSegmentLangs() {
        if (mDocHandle != 0) {
            ArrayList<Boolean> list = new ArrayList<Boolean>();
            int[] a = nativeGetInputSegmentLangs(mDocHandle);
            for (int i : a) {
                list.add(i == 1);
            }
            return list;
        }
        return null;
    }

    public String getSegmentText() {
        if (mDocHandle != 0) {
            return nativeGetInputSegmentText(mDocHandle);
        }
        return null;
    }

    public boolean addInputTrace(short[] trace) {
        if (mDocHandle != 0) {
            return nativeDocAddInputTrace(mDocHandle, trace);
        }
        return false;
    }

    public boolean insertInputTrace(int offset, short[] trace) {
	    if (mDocHandle != 0) {
		    return nativeDocInsertInputTrace(mDocHandle, offset, trace);
	    }
	    return false;
    }

    public boolean insertSpace(int offset) {
        if (mDocHandle != 0) {
            return nativeDocInsertSpace(mDocHandle, offset);
        }
        return false;
    }

    public boolean insertCarriageReturn(int offset) {
        if (mDocHandle != 0) {
            return nativeDocInsertCarriageReturn(mDocHandle, offset);
        }
        return false;
    }
    
    // end not included.
    public boolean removeWords(int start, int end) {
        if (mDocHandle != 0) {
            return nativeDocRemoveWords(mDocHandle, start, end - 1);
        }
        return false;
    }
    
    public ArrayList<StrokeRange> searchKeywordRanges(String keyword) {
        if (mDocHandle != 0) {
            int[] searchResult = nativeSearchKeywordRanges(mDocHandle, keyword);
            return intsToTraceRanges(searchResult);
        }
        return null;
    }

    public ArrayList<Rect> searchKeywordRects(String keyword) {
        if (mDocHandle != 0) {
            short[] searchResult = nativeSearchKeywordRects(mDocHandle, keyword);
            return shortsToRects(searchResult);
        }
        return null;
    }

    public ArrayList<StrokeRange> searchTraceRanges(short[] trace) {
        if (mDocHandle != 0) {
            int[] searchResult = nativeSearchTraceRanges(mDocHandle, trace);
            return intsToTraceRanges(searchResult);
        }
        return null;
    }
    
    public ArrayList<Rect> searchTraceRects(short[] trace) {
        if (mDocHandle != 0) {
            short[] searchResult = nativeSearchTraceRects(mDocHandle, trace);
            return shortsToRects(searchResult);
        }
        return null;
    }

    public String getRecognizedText() {
        if (mDocHandle != 0) {
            return nativeGetDocSegmentText(mDocHandle);
        }
        return null;
    }

    private ArrayList<Rect> shortsToRects(short[] shorts) {
        ArrayList<Rect> rects = new ArrayList<Rect>();
        if (shorts == null)
            return rects;
        for (int i = 0; i < shorts.length; i += 4) {
            rects.add(new Rect(shorts[i], shorts[i+1], shorts[i+2], shorts[i+3]));
        }
        return rects;
    }
    
    private ArrayList<StrokeRange> intsToTraceRanges(int[] ints) {
        ArrayList<StrokeRange> ranges = new ArrayList<StrokeRange>();
        if (ints != null) {
            for (int i = 0; i < ints.length; i += 4) {
                ranges.add(new StrokeRange(ints[i], ints[i+1], ints[i+2], ints[i+3]));
            }
        }
        return ranges;
    }
}
