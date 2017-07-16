import com.application.gui.abstracts.exceptions.DuplicateMasterThreadException;
import com.application.gui.abstracts.exceptions.DuplicateSlaveThreadException;
import com.application.gui.abstracts.exceptions.UnknownMasterThreadException;
import com.application.gui.controllers.WindowsController;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.*;

import static org.junit.Assert.*;

@RunWith(MockitoJUnitRunner.class)
public class WindowsControllerTester {
    
    @Test
    public void addMasterThreadCorrectlyDetectsDuplicates() {
        Thread masterThread = new Thread();
        boolean exceptionExpected = false;
        boolean exceptionUnexpected = false;
        
        WindowsController.reset();
        
        try {
            WindowsController.addMasterThread(masterThread);
        }
        catch (Exception e) {
            exceptionUnexpected = true;
        }
        
        try {
            WindowsController.addMasterThread(masterThread);
            WindowsController.addMasterThread(masterThread);
        }
        catch (DuplicateMasterThreadException e) {
            exceptionExpected = true;
        }
        
        assertTrue(exceptionExpected);
        assertFalse(exceptionUnexpected);
    }
    
    @Test
    public void addMasterThreadMethodWorksCorrectly() {
        Thread firstMaster = new Thread();
        Thread secondMaster = new Thread();
        boolean exceptionUnexpected = false;
        
        WindowsController.reset();
        
        try {
            WindowsController.addMasterThread(firstMaster);
            WindowsController.addMasterThread(secondMaster);
        }
        catch (Exception e){
            exceptionUnexpected = true;
        }
        
        Map map = WindowsController.getMastersThreads();
        assertTrue(map.containsKey(firstMaster));
        assertTrue(map.containsKey(secondMaster));
        assertEquals(map.size(), 2);
        assertFalse(exceptionUnexpected);
    }
    
    @Test
    public void addSlaveThreadsDetectsDuplicates() {
        Thread master = new Thread();
        Thread slave = new Thread();
        boolean exceptionExpected = false;
        boolean exceptionUnexpected = false;
        
        WindowsController.reset();
        
        try {
            WindowsController.addMasterThread(master);
            WindowsController.addSlaveThread(master, slave);
        }
        catch (Exception e) {
            exceptionUnexpected = true;
        }
        
        try {
            WindowsController.addSlaveThread(master, slave);
        }
        catch (DuplicateSlaveThreadException e) {
            exceptionExpected = true;
        }
        catch (UnknownMasterThreadException e) {
            exceptionUnexpected = true;
        }
        
        assertFalse(exceptionUnexpected);
        assertTrue(exceptionExpected);
    }
    
    @Test
    public void addSlaveThreadDetectsUnknownMasterThread() {
        Thread master = new Thread();
        Thread secondMaster = new Thread();
        Thread slave = new Thread();
        boolean exceptionExpected = false;
        boolean exceptionUnexpected = false;
        
        WindowsController.reset();
        
        try {
            WindowsController.addMasterThread(master);
        }
        catch (DuplicateMasterThreadException e) {
            exceptionUnexpected = true;
        }
    
        try {
            WindowsController.addSlaveThread(secondMaster, slave);
        }
        catch (DuplicateSlaveThreadException e) {
            exceptionUnexpected = true;
        } catch (UnknownMasterThreadException e) {
            exceptionExpected = true;
        }
    
        Map map = WindowsController.getMastersThreads();
        Set set = (Set)map.get(master);
        
        assertFalse(exceptionUnexpected);
        assertTrue(exceptionExpected);
        assertEquals(1, map.size());
        assertEquals(0, set.size());
    }
    
    @Test
    public void addSlaveThreadsDetectsDuplicateSlaves() {
        Thread master = new Thread();
        Thread slave = new Thread();
        boolean exceptionUnexpected = false;
        boolean exceptionExpected = false;
    
        WindowsController.reset();
    
        try {
            WindowsController.addMasterThread(master);
            WindowsController.addSlaveThread(master, slave);
        } catch (Exception e) {
            exceptionUnexpected = true;
        }
        
        try {
            WindowsController.addSlaveThread(master, slave);
        } catch (DuplicateSlaveThreadException e) {
            exceptionExpected = true;
        } catch (UnknownMasterThreadException e) {
            exceptionUnexpected = true;
        }
    
        Map map = WindowsController.getMastersThreads();
        Set set = (Set)map.get(master);
        assertTrue(exceptionExpected);
        assertFalse(exceptionUnexpected);
        assertEquals(map.size(), 1);
        assertEquals(set.size(), 1);
    }
    
    @Test
    public void addSlaveThreadsWorksCorrectly() {
        Thread master = new Thread();
        Thread slave = new Thread();
        Thread secondSlave = new Thread();
        boolean exceptionUnexpected = false;
        
        WindowsController.reset();
        
        try {
            WindowsController.addMasterThread(master);
            WindowsController.addSlaveThread(master, slave);
            WindowsController.addSlaveThread(master, secondSlave);
        } catch (Exception e) {
            exceptionUnexpected = true;
        }
    
        Map map = WindowsController.getMastersThreads();
        Set set = (Set)map.get(master);
        assertFalse(exceptionUnexpected);
        assertTrue(set.contains(slave));
        assertTrue(set.contains(secondSlave));
        assertEquals(2, set.size());
    }
    
    
}
