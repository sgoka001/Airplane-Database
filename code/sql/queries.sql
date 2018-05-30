--Add Plane: Ask the user for details of a plane and add it to the DB
INSERT INTO Plane (make, model, age, seats)
VALUES ('Boeing', '747', 1984, 250);

--Add Pilot: Ask the user for details of a pilot and add it to the DB
INSERT INTO Pilot (fullname, nationality)
VALUES ('Mr. Pilot Person', 'American');

--Add Flight: Ask the user for details of a flight and add it to the DB
INSERT INTO Flight (cost, num_sold, num_stops, actual_departure_date,
	actual_arrival_date, arrival_airport, departure_airport)
VALUES (65, 0, 1, '10-15-18 12:00', '10-15-18 13:10', 'SMF', 'ONT');

--Add Technician: Ask user for details of a technician and add it to the DB
INSERT INTO Technician (full_name)
VALUES ('Mr. Technician Guy');

--Book Flight: Given a customer and flight that he/she wants to book, 
--determine the status of the reservation (Waitlisted/Confirmed/Reserved) 
--and add the reservation to the database with appropriate status.
INSERT INTO Reservation (cid, fid, status)
VALUES (12, 1234, 'C');--(SELECT (CASE 
--	WHEN Flight.actual_arrival_date < now() THEN 'C' 
--	WHEN (Plane.seats - Flight.num_sold) = 0 THEN 'W' ELSE 'R')
--	FROM Flight
--	INNER JOIN FlightInfo fi ON fi.flight_id = Flight.fnum
--	INNER JOIN Plane ON Plane.id = fi.plane_id
--	WHERE Flight.fnum = 1234));

--List number of available seats for a given flight: Given a flight number 
--and a departure date, find the number of available seats in the flight.
SELECT (Plane.seats - Flight.num_sold) AS SeatsAvailable FROM Flight
INNER JOIN Schedule ON Schedule.flightNum = Flight.fnum
INNER JOIN FlightInfo fi ON fi.flight_id = Flight.fnum
INNER JOIN Plane ON Plane.id = fi.plane_id
WHERE Flight.fnum = 0 AND Schedule.departure_time = '2014-04-18 14:03';

--List total number of repairs per plane in descending order: Return the 
--list of planes in decreasing order of number of repairs that have been 
--made on the planes.
SELECT Plane.id, COUNT(Repairs.rid) AS RepairCount FROM Plane
INNER JOIN Repairs ON Repairs.plane_id = Plane.id
GROUP BY Plane.id
ORDER BY RepairCount DESC;

--List total number of repairs per year in ascending order:  Return 
--the years with the number of repairs made in those years in ascending 
--order of number of repairs per year.
SELECT EXTRACT(YEAR FROM repair_date) AS RepairYear, COUNT(rid) AS RepairCount FROM Repairs
GROUP BY EXTRACT(YEAR FROM repair_date)
ORDER BY RepairCount ASC;

--Find total number of passengers with a given status:  For a given 
--flight and passenger status, return the number of passengers with
--the given status.
SELECT COUNT(Customer.id) FROM Customer
INNER JOIN Reservation ON Reservation.cid = Customer.id
WHERE Reservation.fid = 0 AND Reservation.status = 'C';


