-- phpMyAdmin SQL Dump
-- version 5.2.1
-- https://www.phpmyadmin.net/
--
-- Host: 127.0.0.1
-- Creato il: Mag 07, 2025 alle 15:13
-- Versione del server: 10.4.32-MariaDB
-- Versione PHP: 8.2.12

SET SQL_MODE = "NO_AUTO_VALUE_ON_ZERO";
START TRANSACTION;
SET time_zone = "+00:00";


/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8mb4 */;

--
-- Database: `hitwaves`
--

-- --------------------------------------------------------

--
-- Struttura della tabella `admin`
--

CREATE TABLE `admin` (
  `id` int(11) NOT NULL,
  `email` varchar(255) NOT NULL,
  `password` varchar(255) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dump dei dati per la tabella `admin`
--

INSERT INTO `admin` (`id`, `email`, `password`) VALUES
(1, 'admin@example.com', '240be518fabd2724ddb6f04eeb1da5967448d7e831c08c8fa822809f74c720a9');

-- --------------------------------------------------------

--
-- Struttura della tabella `artist`
--

CREATE TABLE `artist` (
  `id` int(11) NOT NULL,
  `name` varchar(255) NOT NULL,
  `image` varchar(255) DEFAULT NULL,
  `record_company_id` int(11) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dump dei dati per la tabella `artist`
--

INSERT INTO `artist` (`id`, `name`, `image`, `record_company_id`) VALUES
(1, 'Nayt', 'nayt.jpeg', 3),
(2, 'Gino', 'gino.png', 4);

-- --------------------------------------------------------

--
-- Struttura della tabella `artist_concert`
--

CREATE TABLE `artist_concert` (
  `id` int(11) NOT NULL,
  `artist_id` int(11) DEFAULT NULL,
  `concert_id` int(11) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dump dei dati per la tabella `artist_concert`
--

INSERT INTO `artist_concert` (`id`, `artist_id`, `concert_id`) VALUES
(1, 1, 1),
(3, 2, 4),
(4, 2, 5),
(5, 1, 7),
(6, 1, 6),
(23, 1, 20),
(24, 2, 20);

-- --------------------------------------------------------

--
-- Struttura della tabella `concert`
--

CREATE TABLE `concert` (
  `id` int(11) NOT NULL,
  `title` varchar(255) NOT NULL,
  `image` varchar(255) DEFAULT NULL,
  `date` date NOT NULL,
  `time` time NOT NULL,
  `place_id` int(11) DEFAULT NULL,
  `record_company_id` int(11) DEFAULT NULL,
  `tour_id` int(11) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dump dei dati per la tabella `concert`
--

INSERT INTO `concert` (`id`, `title`, `image`, `date`, `time`, `place_id`, `record_company_id`, `tour_id`) VALUES
(1, 'La La La (in RE#)', 'lalala.jpg', '2025-04-30', '21:00:00', 5, 3, NULL),
(4, 'Ciao1', NULL, '2025-04-30', '15:50:41', 6, 3, 2),
(5, 'CIAO2', NULL, '2025-04-30', '15:51:11', 6, 3, 2),
(6, 'PASSATO', 'p.png', '2025-04-01', '18:06:39', 5, 3, 3),
(7, 'prova', NULL, '2025-05-31', '16:17:41', 5, 4, 4),
(20, 'Concerto di prova', '20.jpg', '2025-06-15', '21:00:00', 6, 3, NULL);

-- --------------------------------------------------------

--
-- Struttura della tabella `likes`
--

CREATE TABLE `likes` (
  `id` int(11) NOT NULL,
  `user_id` int(11) DEFAULT NULL,
  `artist_id` int(11) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dump dei dati per la tabella `likes`
--

INSERT INTO `likes` (`id`, `user_id`, `artist_id`) VALUES
(2, 6, 1),
(5, 6, 2);

-- --------------------------------------------------------

--
-- Struttura della tabella `notification`
--

CREATE TABLE `notification` (
  `id` int(11) NOT NULL,
  `title` varchar(255) NOT NULL,
  `description` text NOT NULL,
  `is_read` tinyint(1) NOT NULL DEFAULT 0,
  `user_id` int(11) DEFAULT NULL,
  `record_company_id` int(11) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dump dei dati per la tabella `notification`
--

INSERT INTO `notification` (`id`, `title`, `description`, `is_read`, `user_id`, `record_company_id`) VALUES
(22, 'New Concert!', 'A new concert has been created that might interest you: Concerto di prova', 0, 6, NULL);

-- --------------------------------------------------------

--
-- Struttura della tabella `place`
--

CREATE TABLE `place` (
  `id` int(11) NOT NULL,
  `name` varchar(255) NOT NULL,
  `address` varchar(255) NOT NULL,
  `latitude` float DEFAULT NULL,
  `longitude` float DEFAULT NULL,
  `email` varchar(255) NOT NULL,
  `telephone` varchar(15) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dump dei dati per la tabella `place`
--

INSERT INTO `place` (`id`, `name`, `address`, `latitude`, `longitude`, `email`, `telephone`) VALUES
(5, 'Zona A', 'Lecce', 0, 0, 'zonaA@gmail.com', '3703131311'),
(6, 'Zona B', 'Via Arturo Carlo Jemolo, 110, 47023 Cesena FC', 44.1447, 12.2386, 'ciao@example.com', '3703158344');

-- --------------------------------------------------------

--
-- Struttura della tabella `record_company`
--

CREATE TABLE `record_company` (
  `id` int(11) NOT NULL,
  `email` varchar(255) NOT NULL,
  `password` varchar(255) NOT NULL,
  `session_token` varchar(255) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dump dei dati per la tabella `record_company`
--

INSERT INTO `record_company` (`id`, `email`, `password`, `session_token`) VALUES
(3, 'ciao@gmail.com', '$2b$12$rM2awtCM8c/tYYWOYezTMuXvncW48Lvv88RVO6e3kt5EkYf6zCuXS', 'eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJjb21wYW55X2lkIjozfQ.jlRGPVO42Qe1julQuud8ocTndzKeF6ZDU8vFX7jwnfA'),
(4, 'esp@ex.com', '$2b$12$rM2awtCM8c/tYYWOYezTMuXvncW48Lvv88RVO6e3kt5EkYf6zCuXS', NULL);

-- --------------------------------------------------------

--
-- Struttura della tabella `review`
--

CREATE TABLE `review` (
  `id` int(11) NOT NULL,
  `rate` int(11) NOT NULL,
  `description` text DEFAULT NULL,
  `user_id` int(11) DEFAULT NULL,
  `concert_id` int(11) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dump dei dati per la tabella `review`
--

INSERT INTO `review` (`id`, `rate`, `description`, `user_id`, `concert_id`) VALUES
(1, 5, 'Bello', 6, 1);

-- --------------------------------------------------------

--
-- Struttura della tabella `seat`
--

CREATE TABLE `seat` (
  `id` int(11) NOT NULL,
  `description` varchar(255) NOT NULL DEFAULT 'Unnumbered',
  `x` float DEFAULT NULL,
  `y` float DEFAULT NULL,
  `sector_id` int(11) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dump dei dati per la tabella `seat`
--

INSERT INTO `seat` (`id`, `description`, `x`, `y`, `sector_id`) VALUES
(1, 'Unnumbered', 243, 177, 2),
(2, 'Unnumbered', 304, 176, 2),
(3, 'Unnumbered', 361, 177, 2),
(4, 'Unnumbered', 243, 177, 4),
(5, 'Unnumbered', 304, 176, 4),
(6, 'Unnumbered', 361, 177, 4);

-- --------------------------------------------------------

--
-- Struttura della tabella `sector`
--

CREATE TABLE `sector` (
  `id` int(11) NOT NULL,
  `name` varchar(255) NOT NULL,
  `x_sx` float DEFAULT NULL,
  `y_sx` float DEFAULT NULL,
  `x_dx` float DEFAULT NULL,
  `y_dx` float DEFAULT NULL,
  `place_id` int(11) DEFAULT NULL,
  `is_stage` tinyint(1) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dump dei dati per la tabella `sector`
--

INSERT INTO `sector` (`id`, `name`, `x_sx`, `y_sx`, `x_dx`, `y_dx`, `place_id`, `is_stage`) VALUES
(1, 'Stage', 201, 54, 412, 115, 5, 1),
(2, 'Sector 2', 200, 147, 415, 210, 5, 0),
(3, 'Stage', 201, 54, 412, 115, 6, 1),
(4, 'Sector 2', 200, 147, 415, 210, 6, 0);

-- --------------------------------------------------------

--
-- Struttura della tabella `ticket`
--

CREATE TABLE `ticket` (
  `id` int(11) NOT NULL,
  `price` decimal(10,2) NOT NULL,
  `validated` tinyint(1) NOT NULL,
  `user_id` int(11) DEFAULT NULL,
  `concert_id` int(11) DEFAULT NULL,
  `seat_id` int(11) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dump dei dati per la tabella `ticket`
--

INSERT INTO `ticket` (`id`, `price`, `validated`, `user_id`, `concert_id`, `seat_id`) VALUES
(1, 20.00, 0, 6, 1, 1),
(2, 20.00, 0, NULL, 1, 2),
(3, 20.00, 0, NULL, 1, 3),
(4, 10.00, 1, 6, 6, 1),
(29, 30.00, 0, 6, 20, 4),
(30, 30.00, 0, NULL, 20, 5),
(31, 30.00, 0, NULL, 20, 6);

-- --------------------------------------------------------

--
-- Struttura della tabella `tour`
--

CREATE TABLE `tour` (
  `id` int(11) NOT NULL,
  `title` varchar(255) NOT NULL,
  `image` varchar(255) NOT NULL,
  `record_company_id` int(11) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dump dei dati per la tabella `tour`
--

INSERT INTO `tour` (`id`, `title`, `image`, `record_company_id`) VALUES
(2, 'Concert 1', 'storie.jpg', 3),
(3, 'TOUR 3', 'tour1.png', 3),
(4, 'Prova Nayt', 'provanayt.png', 4);

-- --------------------------------------------------------

--
-- Struttura della tabella `user`
--

CREATE TABLE `user` (
  `id` int(11) NOT NULL,
  `username` varchar(255) NOT NULL,
  `email` varchar(255) NOT NULL,
  `password` varchar(255) NOT NULL,
  `name` varchar(255) NOT NULL,
  `surname` varchar(255) NOT NULL,
  `birthdate` date NOT NULL,
  `image` varchar(255) DEFAULT NULL,
  `refunds` decimal(10,2) NOT NULL,
  `session_token` varchar(255) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dump dei dati per la tabella `user`
--

INSERT INTO `user` (`id`, `username`, `email`, `password`, `name`, `surname`, `birthdate`, `image`, `refunds`, `session_token`) VALUES
(6, 'ciccio1', 'ciccio@example.com', '$2b$12$3MV6ExEzNs18LqmATkhnYu8HZS26FiViItol8358PnHb5jsBEJxTS', 'Ciccio', 'Pippo', '2000-01-01', 'ciccio1.jpeg', 90.00, 'eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJ1c2VyX2lkIjo2fQ.aR2HUmYnDNH0JP0s1touqOiMEv5D7YN6Re9LTzpEalU');

--
-- Indici per le tabelle scaricate
--

--
-- Indici per le tabelle `admin`
--
ALTER TABLE `admin`
  ADD PRIMARY KEY (`id`),
  ADD UNIQUE KEY `email` (`email`);

--
-- Indici per le tabelle `artist`
--
ALTER TABLE `artist`
  ADD PRIMARY KEY (`id`),
  ADD UNIQUE KEY `name` (`name`),
  ADD KEY `record_company_id` (`record_company_id`);

--
-- Indici per le tabelle `artist_concert`
--
ALTER TABLE `artist_concert`
  ADD PRIMARY KEY (`id`),
  ADD KEY `artist_id` (`artist_id`),
  ADD KEY `concert_id` (`concert_id`);

--
-- Indici per le tabelle `concert`
--
ALTER TABLE `concert`
  ADD PRIMARY KEY (`id`),
  ADD KEY `place_id` (`place_id`),
  ADD KEY `record_company_id` (`record_company_id`),
  ADD KEY `tour_id` (`tour_id`);

--
-- Indici per le tabelle `likes`
--
ALTER TABLE `likes`
  ADD PRIMARY KEY (`id`),
  ADD KEY `user_id` (`user_id`),
  ADD KEY `artist_id` (`artist_id`);

--
-- Indici per le tabelle `notification`
--
ALTER TABLE `notification`
  ADD PRIMARY KEY (`id`),
  ADD KEY `user_id` (`user_id`),
  ADD KEY `record_company_id` (`record_company_id`);

--
-- Indici per le tabelle `place`
--
ALTER TABLE `place`
  ADD PRIMARY KEY (`id`);

--
-- Indici per le tabelle `record_company`
--
ALTER TABLE `record_company`
  ADD PRIMARY KEY (`id`),
  ADD UNIQUE KEY `email` (`email`);

--
-- Indici per le tabelle `review`
--
ALTER TABLE `review`
  ADD PRIMARY KEY (`id`),
  ADD KEY `user_id` (`user_id`),
  ADD KEY `concert_id` (`concert_id`);

--
-- Indici per le tabelle `seat`
--
ALTER TABLE `seat`
  ADD PRIMARY KEY (`id`),
  ADD KEY `sector_id` (`sector_id`);

--
-- Indici per le tabelle `sector`
--
ALTER TABLE `sector`
  ADD PRIMARY KEY (`id`),
  ADD KEY `place_id` (`place_id`);

--
-- Indici per le tabelle `ticket`
--
ALTER TABLE `ticket`
  ADD PRIMARY KEY (`id`),
  ADD KEY `user_id` (`user_id`),
  ADD KEY `concert_id` (`concert_id`),
  ADD KEY `seat_id` (`seat_id`);

--
-- Indici per le tabelle `tour`
--
ALTER TABLE `tour`
  ADD PRIMARY KEY (`id`),
  ADD KEY `record_company_id` (`record_company_id`);

--
-- Indici per le tabelle `user`
--
ALTER TABLE `user`
  ADD PRIMARY KEY (`id`),
  ADD UNIQUE KEY `email` (`email`),
  ADD UNIQUE KEY `username` (`username`);

--
-- AUTO_INCREMENT per le tabelle scaricate
--

--
-- AUTO_INCREMENT per la tabella `admin`
--
ALTER TABLE `admin`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=2;

--
-- AUTO_INCREMENT per la tabella `artist`
--
ALTER TABLE `artist`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=3;

--
-- AUTO_INCREMENT per la tabella `artist_concert`
--
ALTER TABLE `artist_concert`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=25;

--
-- AUTO_INCREMENT per la tabella `concert`
--
ALTER TABLE `concert`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=21;

--
-- AUTO_INCREMENT per la tabella `likes`
--
ALTER TABLE `likes`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=6;

--
-- AUTO_INCREMENT per la tabella `notification`
--
ALTER TABLE `notification`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=23;

--
-- AUTO_INCREMENT per la tabella `place`
--
ALTER TABLE `place`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=7;

--
-- AUTO_INCREMENT per la tabella `record_company`
--
ALTER TABLE `record_company`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=5;

--
-- AUTO_INCREMENT per la tabella `review`
--
ALTER TABLE `review`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=3;

--
-- AUTO_INCREMENT per la tabella `seat`
--
ALTER TABLE `seat`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=7;

--
-- AUTO_INCREMENT per la tabella `sector`
--
ALTER TABLE `sector`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=5;

--
-- AUTO_INCREMENT per la tabella `ticket`
--
ALTER TABLE `ticket`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=32;

--
-- AUTO_INCREMENT per la tabella `tour`
--
ALTER TABLE `tour`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=5;

--
-- AUTO_INCREMENT per la tabella `user`
--
ALTER TABLE `user`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=9;

--
-- Limiti per le tabelle scaricate
--

--
-- Limiti per la tabella `artist`
--
ALTER TABLE `artist`
  ADD CONSTRAINT `artist_ibfk_1` FOREIGN KEY (`record_company_id`) REFERENCES `record_company` (`id`);

--
-- Limiti per la tabella `artist_concert`
--
ALTER TABLE `artist_concert`
  ADD CONSTRAINT `artist_concert_ibfk_1` FOREIGN KEY (`artist_id`) REFERENCES `artist` (`id`),
  ADD CONSTRAINT `artist_concert_ibfk_2` FOREIGN KEY (`concert_id`) REFERENCES `concert` (`id`);

--
-- Limiti per la tabella `concert`
--
ALTER TABLE `concert`
  ADD CONSTRAINT `concert_ibfk_1` FOREIGN KEY (`place_id`) REFERENCES `place` (`id`),
  ADD CONSTRAINT `concert_ibfk_2` FOREIGN KEY (`record_company_id`) REFERENCES `record_company` (`id`),
  ADD CONSTRAINT `concert_ibfk_3` FOREIGN KEY (`tour_id`) REFERENCES `tour` (`id`);

--
-- Limiti per la tabella `likes`
--
ALTER TABLE `likes`
  ADD CONSTRAINT `likes_ibfk_1` FOREIGN KEY (`user_id`) REFERENCES `user` (`id`),
  ADD CONSTRAINT `likes_ibfk_2` FOREIGN KEY (`artist_id`) REFERENCES `artist` (`id`);

--
-- Limiti per la tabella `notification`
--
ALTER TABLE `notification`
  ADD CONSTRAINT `notification_ibfk_1` FOREIGN KEY (`user_id`) REFERENCES `user` (`id`),
  ADD CONSTRAINT `notification_ibfk_2` FOREIGN KEY (`record_company_id`) REFERENCES `record_company` (`id`);

--
-- Limiti per la tabella `review`
--
ALTER TABLE `review`
  ADD CONSTRAINT `review_ibfk_1` FOREIGN KEY (`user_id`) REFERENCES `user` (`id`),
  ADD CONSTRAINT `review_ibfk_2` FOREIGN KEY (`concert_id`) REFERENCES `concert` (`id`);

--
-- Limiti per la tabella `seat`
--
ALTER TABLE `seat`
  ADD CONSTRAINT `seat_ibfk_1` FOREIGN KEY (`sector_id`) REFERENCES `sector` (`id`);

--
-- Limiti per la tabella `sector`
--
ALTER TABLE `sector`
  ADD CONSTRAINT `sector_ibfk_1` FOREIGN KEY (`place_id`) REFERENCES `place` (`id`);

--
-- Limiti per la tabella `ticket`
--
ALTER TABLE `ticket`
  ADD CONSTRAINT `ticket_ibfk_1` FOREIGN KEY (`user_id`) REFERENCES `user` (`id`),
  ADD CONSTRAINT `ticket_ibfk_2` FOREIGN KEY (`concert_id`) REFERENCES `concert` (`id`),
  ADD CONSTRAINT `ticket_ibfk_3` FOREIGN KEY (`seat_id`) REFERENCES `seat` (`id`);

--
-- Limiti per la tabella `tour`
--
ALTER TABLE `tour`
  ADD CONSTRAINT `tour_ibfk_1` FOREIGN KEY (`record_company_id`) REFERENCES `record_company` (`id`);
COMMIT;

/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
