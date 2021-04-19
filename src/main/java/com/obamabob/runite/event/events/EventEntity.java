package com.obamabob.runite.event.events;

import com.obamabob.runite.event.Event;
import net.minecraft.entity.Entity;

 public class EventEntity extends Event {

        private Entity entity;

        public EventEntity(Entity entity) {
            super();
            this.entity = entity;
        }

        public Entity getEntity() {
            return entity;
        }

        public static class EntityCollision extends EventEntity {
            double x,y,z;

            public EntityCollision(Entity entity, double x, double y, double z) {
                super(entity);
                this.x = x;
                this.y = y;
                this.z = z;
            }

            public double getX() {
                return x;
            }

            public double getY() {
                return y;
            }

            public double getZ() {
                return z;
            }

            public void setX(double x) {
                this.x = x;
            }

            public void setY(double y) {
                this.y = y;
            }

            public void setZ(double z) {
                this.z = z;
            }
        }

    }
