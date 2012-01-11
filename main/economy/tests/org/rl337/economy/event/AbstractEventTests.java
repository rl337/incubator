package org.rl337.economy.event;

import junit.framework.TestCase;

import org.rl337.economy.EntityFactory;
import org.rl337.economy.KeyFactory;
import org.rl337.economy.SimulationProxy;
import org.rl337.economy.KeyFactory.EntityKey;
import org.rl337.economy.KeyFactory.KeyType;
import org.rl337.economy.KeyFactory.Tick;
import org.rl337.economy.data.entity.Entity;
import org.rl337.economy.data.entity.MarketUserImpl;
import org.rl337.economy.event.Event.EventException;

import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Inject;
import com.google.inject.Injector;
import com.google.inject.name.Names;

public class AbstractEventTests extends TestCase {
    private Injector mInjector;
    private TestSimulationProxy mSimulationProxy;
    private Entity mEntity;
    
    public void setUp() {
        mInjector = Guice.createInjector(
            new AbstractModule() {
                @Override
                protected void configure() {
                    bind(KeyFactory.class).asEagerSingleton();
                    bind(EntityFactory.class).asEagerSingleton();
                    bind(Class.class).annotatedWith(Names.named("entityFactory.entityClass")).toInstance(MarketUserImpl.class);
                }
            }
        );
    
        mSimulationProxy = mInjector.getInstance(TestSimulationProxy.class);
        EntityKey k = mSimulationProxy.addEntity("foobie");
        mEntity = mSimulationProxy.getEntity(k);
        
    }

    public void testExecute() throws Exception {
        EntityKey k = mSimulationProxy.addEntity("foobie");
        TestEvent event = new TestEvent(k, mSimulationProxy.getCurrentTick(), false);
        
        assertFalse("Event should start out nont executed.", event.executed());
        assertEquals("Event should start with a null executed tick.", null, event.getExecutedOnTick());
        assertFalse("Event should report not success before execution", event.success());
        
        event.execute(mSimulationProxy);
        
        assertTrue("Event should be executed after execution", event.executed());
        assertEquals("Event should have an executed tick of 0", 0,  event.getExecutedOnTick().getValue());
        assertTrue("Event should report success", event.success());
    }
    
    public void testExecuteWithException() throws Exception {
        TestEvent event = new TestEvent(mEntity.getKey(), mSimulationProxy.getCurrentTick(), true);
        
        assertFalse("Event should start out nont executed.", event.executed());
        assertEquals("Event should start with a null executed tick.", null, event.getExecutedOnTick());
        assertFalse("Event should report not success before execution", event.success());
        
        try {
            event.execute(mSimulationProxy);
        } catch (EventException e) {
            // expected behavior
        }
        
        assertTrue("Event should be executed after execution", event.executed());
        assertEquals("Event should have an executed tick of 0", 0,  event.getExecutedOnTick().getValue());
        assertFalse("Event should report not success after exception thrown", event.success());
    }
    
    private static class TestSimulationProxy implements SimulationProxy {
        @Inject
        private KeyFactory mKeyFactory;
        
        @Inject
        private EntityFactory mEntityFactory;

        @Override
        public boolean addEvent(Event e) {
            return false;
        }

        @Override
        public Tick getCurrentTick() {
            return mKeyFactory.currentKey(KeyType.Tick);
        }

        @Override
        public EntityKey addEntity(String entityName) {
            return mEntityFactory.newEntity(entityName).getKey();
        }

        @Override
        public Entity getEntity(EntityKey entityKey) {
            
            return mEntityFactory.get(entityKey);
        }
        
    }
    
    @SuppressWarnings("serial")
    private static class TestEvent extends AbstractEvent {
        private boolean mThrowsException;
        
        public TestEvent(EntityKey k, Tick tickToExecute, boolean except) {
            super(k, tickToExecute);
            mThrowsException = except;
        }

        @Override
        public void protectedExecute(Entity e, SimulationProxy p) throws Exception {
            if (mThrowsException) {
                throw new Exception("Blah");
            }
        }
        
    }
}
