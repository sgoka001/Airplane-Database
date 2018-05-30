DROP INDEX IF EXISTS customer_id_index;
DROP INDEX IF EXISTS pilot_id_index;
DROP INDEX IF EXISTS flight_id_index;
DROP INDEX IF EXISTS plane_id_index;
DROP INDEX IF EXISTS technician_id_index;
DROP INDEX IF EXISTS reservation_id_index;
DROP INDEX IF EXISTS repairs_id_index;
DROP INDEX IF EXISTS schedule_id_index;


CREATE INDEX customer_id_index
ON Customer
USING BTREE
(id);

CREATE INDEX pilot_id_index
ON Pilot
USING BTREE
(id);

CREATE INDEX flight_id_index
ON Flight
USING BTREE
(fnum);

CREATE INDEX plane_id_index
ON Plane
USING BTREE
(id);

CREATE INDEX technician_id_index
ON Technician
USING BTREE
(id);

CREATE INDEX reservation_id_index
ON Reservation
USING BTREE
(rnum);

CREATE INDEX repairs_id_index
ON Repairs
USING BTREE
(rid);

CREATE INDEX  schedule_id_index
ON Schedule
USING BTREE
(id);
