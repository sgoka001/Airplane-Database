DROP SEQUENCE IF EXISTS plane_seq;
DROP SEQUENCE IF EXISTS pilot_seq;
DROP SEQUENCE IF EXISTS flight_seq;
DROP SEQUENCE IF EXISTS technician_seq;
DROP SEQUENCE IF EXISTS reservation_seq;
CREATE SEQUENCE plane_seq START WITH 67;
CREATE SEQUENCE pilot_seq START WITH 250;
CREATE SEQUENCE flight_seq START WITH 2000;
CREATE SEQUENCE technician_seq START WITH 250;
CREATE SEQUENCE reservation_seq START WITH 9999;

CREATE OR REPLACE FUNCTION plane_incr () RETURNS trigger AS $plane_incr$
BEGIN
	NEW.id := nextval('plane_seq');
	RETURN NEW;
END;
$plane_incr$ LANGUAGE plpgsql;

CREATE OR REPLACE FUNCTION pilot_incr () RETURNS trigger AS $pilot_incr$
BEGIN
	NEW.id := nextval('pilot_seq');
	RETURN NEW;
END;
$pilot_incr$ LANGUAGE plpgsql;

CREATE OR REPLACE FUNCTION flight_incr () RETURNS trigger AS $flight_incr$
BEGIN
	NEW.fnum := nextval('flight_seq');
	RETURN NEW;
END;
$flight_incr$ LANGUAGE plpgsql;

CREATE OR REPLACE FUNCTION technician_incr () RETURNS trigger AS $technician_incr$
BEGIN
	NEW.id := nextval('technician_seq');
	RETURN NEW;
END;
$technician_incr$ LANGUAGE plpgsql;

CREATE OR REPLACE FUNCTION reservation_incr () RETURNS trigger AS $reservation_incr$
BEGIN
	NEW.rnum := nextval('reservation_seq');
	RETURN NEW;
END;
$reservation_incr$ LANGUAGE plpgsql;


DROP TRIGGER IF EXISTS plane_trig ON Plane;
CREATE TRIGGER plane_trig BEFORE INSERT
ON Plane FOR EACH ROW
EXECUTE PROCEDURE plane_incr () ;

DROP TRIGGER IF EXISTS pilot_trig ON Pilot;
CREATE TRIGGER pilot_trig BEFORE INSERT
ON Pilot FOR EACH ROW
EXECUTE PROCEDURE pilot_incr () ;

DROP TRIGGER IF EXISTS flight_trig ON Flight;
CREATE TRIGGER flight_trig BEFORE INSERT
ON Flight FOR EACH ROW
EXECUTE PROCEDURE flight_incr () ;

DROP TRIGGER IF EXISTS technician_trig ON Technician;
CREATE TRIGGER technician_trig BEFORE INSERT
ON Technician FOR EACH ROW
EXECUTE PROCEDURE technician_incr () ;

DROP TRIGGER IF EXISTS reservation_trig ON Reservation;
CREATE TRIGGER reservation_trig BEFORE INSERT
ON Reservation FOR EACH ROW
EXECUTE PROCEDURE reservation_incr () ;
