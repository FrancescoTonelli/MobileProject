-- phpMyAdmin SQL Dump
-- version 5.2.1
-- https://www.phpmyadmin.net/
--
-- Host: 127.0.0.1
-- Creato il: Mag 27, 2025 alle 16:07
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
(24, 2, 20),
(35, 2, 28);

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
(1, 'La La La (in RE#)', 'lalala.jpg', '2026-04-30', '21:00:00', 5, 3, NULL),
(4, 'Ciao1', NULL, '2025-04-30', '15:50:41', 6, 3, 2),
(5, 'CIAO2', NULL, '2025-04-30', '15:51:11', 6, 3, 2),
(6, 'PASSATO', NULL, '2025-04-01', '18:06:39', 5, 3, 3),
(7, 'prova', NULL, '2025-05-31', '16:17:41', 5, 4, 4),
(20, 'Concerto di prova', '20.jpg', '2025-06-15', '21:00:00', 6, 3, NULL),
(24, 'Concerto di prova', '24.jpg', '2025-07-15', '21:00:00', 5, 3, NULL),
(28, 'Tanti Posti', '28.png', '2025-07-01', '21:00:00', 7, 3, NULL);

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
(14, 11, 1),
(15, 6, 1),
(16, 6, 2);

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
(25, 'New Tour!', 'A new tour has been created that might interest you: Tour Estivo 2025', 1, 6, NULL),
(26, 'Tour Canceled', 'The tour you had a ticket for has been canceled. A refund has been issued to your account.', 1, 6, NULL),
(27, 'Ciao', 'Ciao', 1, 6, NULL),
(28, 'New Concert!', 'A new concert has been created that might interest you: Tanti Posti', 1, 6, NULL);

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
(6, 'Zona B', 'Via Arturo Carlo Jemolo, 110, 47023 Cesena FC', 44.1447, 12.2386, 'ciao@example.com', '3703158344'),
(7, 'Posto Ampio', 'Via Cesare Pavese, 50, 47521 Cesena FC', 44.1473, 12.2352, 'tutto@example.com', '3703131311');

-- --------------------------------------------------------

--
-- Struttura della tabella `record_company`
--

CREATE TABLE `record_company` (
  `id` int(11) NOT NULL,
  `email` varchar(255) NOT NULL,
  `password` varchar(255) NOT NULL,
  `session_token` varchar(255) DEFAULT NULL,
  `fcm_token` varchar(255) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dump dei dati per la tabella `record_company`
--

INSERT INTO `record_company` (`id`, `email`, `password`, `session_token`, `fcm_token`) VALUES
(3, 'alpha@example.com', '$2b$12$rM2awtCM8c/tYYWOYezTMuXvncW48Lvv88RVO6e3kt5EkYf6zCuXS', 'eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJjb21wYW55X2lkIjozfQ.jlRGPVO42Qe1julQuud8ocTndzKeF6ZDU8vFX7jwnfA', NULL),
(4, 'esp@ex.com', '$2b$12$rM2awtCM8c/tYYWOYezTMuXvncW48Lvv88RVO6e3kt5EkYf6zCuXS', NULL, NULL);

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
(13, 5, 'wow', 6, 6);

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
(6, 'Unnumbered', 361, 177, 4),
(7, 'Unnumbered', 207, 308, 6),
(8, 'Unnumbered', 217, 308, 6),
(9, 'Unnumbered', 227, 308, 6),
(10, 'Unnumbered', 237, 308, 6),
(11, 'Unnumbered', 247, 308, 6),
(12, 'Unnumbered', 257, 308, 6),
(13, 'Unnumbered', 267, 308, 6),
(14, 'Unnumbered', 277, 308, 6),
(15, 'Unnumbered', 287, 308, 6),
(16, 'Unnumbered', 297, 308, 6),
(17, 'Unnumbered', 307, 308, 6),
(18, 'Unnumbered', 317, 308, 6),
(19, 'Unnumbered', 327, 308, 6),
(20, 'Unnumbered', 337, 308, 6),
(21, 'Unnumbered', 347, 308, 6),
(22, 'Unnumbered', 357, 308, 6),
(23, 'Unnumbered', 367, 308, 6),
(24, 'Unnumbered', 377, 308, 6),
(25, 'Unnumbered', 387, 308, 6),
(26, 'Unnumbered', 397, 308, 6),
(27, 'Unnumbered', 407, 308, 6),
(28, 'Unnumbered', 417, 308, 6),
(29, 'Unnumbered', 427, 308, 6),
(30, 'Unnumbered', 437, 308, 6),
(31, 'Unnumbered', 447, 308, 6),
(32, 'Unnumbered', 457, 308, 6),
(33, 'Unnumbered', 207, 318, 6),
(34, 'Unnumbered', 217, 318, 6),
(35, 'Unnumbered', 227, 318, 6),
(36, 'Unnumbered', 237, 318, 6),
(37, 'Unnumbered', 247, 318, 6),
(38, 'Unnumbered', 257, 318, 6),
(39, 'Unnumbered', 267, 318, 6),
(40, 'Unnumbered', 277, 318, 6),
(41, 'Unnumbered', 287, 318, 6),
(42, 'Unnumbered', 297, 318, 6),
(43, 'Unnumbered', 307, 318, 6),
(44, 'Unnumbered', 317, 318, 6),
(45, 'Unnumbered', 327, 318, 6),
(46, 'Unnumbered', 337, 318, 6),
(47, 'Unnumbered', 347, 318, 6),
(48, 'Unnumbered', 357, 318, 6),
(49, 'Unnumbered', 367, 318, 6),
(50, 'Unnumbered', 377, 318, 6),
(51, 'Unnumbered', 387, 318, 6),
(52, 'Unnumbered', 397, 318, 6),
(53, 'Unnumbered', 407, 318, 6),
(54, 'Unnumbered', 417, 318, 6),
(55, 'Unnumbered', 427, 318, 6),
(56, 'Unnumbered', 437, 318, 6),
(57, 'Unnumbered', 447, 318, 6),
(58, 'Unnumbered', 457, 318, 6),
(59, 'Unnumbered', 207, 328, 6),
(60, 'Unnumbered', 217, 328, 6),
(61, 'Unnumbered', 227, 328, 6),
(62, 'Unnumbered', 237, 328, 6),
(63, 'Unnumbered', 247, 328, 6),
(64, 'Unnumbered', 257, 328, 6),
(65, 'Unnumbered', 267, 328, 6),
(66, 'Unnumbered', 277, 328, 6),
(67, 'Unnumbered', 287, 328, 6),
(68, 'Unnumbered', 297, 328, 6),
(69, 'Unnumbered', 307, 328, 6),
(70, 'Unnumbered', 317, 328, 6),
(71, 'Unnumbered', 327, 328, 6),
(72, 'Unnumbered', 337, 328, 6),
(73, 'Unnumbered', 347, 328, 6),
(74, 'Unnumbered', 357, 328, 6),
(75, 'Unnumbered', 367, 328, 6),
(76, 'Unnumbered', 377, 328, 6),
(77, 'Unnumbered', 387, 328, 6),
(78, 'Unnumbered', 397, 328, 6),
(79, 'Unnumbered', 407, 328, 6),
(80, 'Unnumbered', 417, 328, 6),
(81, 'Unnumbered', 427, 328, 6),
(82, 'Unnumbered', 437, 328, 6),
(83, 'Unnumbered', 447, 328, 6),
(84, 'Unnumbered', 457, 328, 6),
(85, 'Unnumbered', 207, 338, 6),
(86, 'Unnumbered', 217, 338, 6),
(87, 'Unnumbered', 227, 338, 6),
(88, 'Unnumbered', 237, 338, 6),
(89, 'Unnumbered', 247, 338, 6),
(90, 'Unnumbered', 257, 338, 6),
(91, 'Unnumbered', 267, 338, 6),
(92, 'Unnumbered', 277, 338, 6),
(93, 'Unnumbered', 287, 338, 6),
(94, 'Unnumbered', 297, 338, 6),
(95, 'Unnumbered', 307, 338, 6),
(96, 'Unnumbered', 317, 338, 6),
(97, 'Unnumbered', 327, 338, 6),
(98, 'Unnumbered', 337, 338, 6),
(99, 'Unnumbered', 347, 338, 6),
(100, 'Unnumbered', 357, 338, 6),
(101, 'Unnumbered', 367, 338, 6),
(102, 'Unnumbered', 377, 338, 6),
(103, 'Unnumbered', 387, 338, 6),
(104, 'Unnumbered', 397, 338, 6),
(105, 'Unnumbered', 407, 338, 6),
(106, 'Unnumbered', 417, 338, 6),
(107, 'Unnumbered', 427, 338, 6),
(108, 'Unnumbered', 437, 338, 6),
(109, 'Unnumbered', 447, 338, 6),
(110, 'Unnumbered', 457, 338, 6),
(111, 'Unnumbered', 207, 348, 6),
(112, 'Unnumbered', 217, 348, 6),
(113, 'Unnumbered', 227, 348, 6),
(114, 'Unnumbered', 237, 348, 6),
(115, 'Unnumbered', 247, 348, 6),
(116, 'Unnumbered', 257, 348, 6),
(117, 'Unnumbered', 267, 348, 6),
(118, 'Unnumbered', 277, 348, 6),
(119, 'Unnumbered', 287, 348, 6),
(120, 'Unnumbered', 297, 348, 6),
(121, 'Unnumbered', 307, 348, 6),
(122, 'Unnumbered', 317, 348, 6),
(123, 'Unnumbered', 327, 348, 6),
(124, 'Unnumbered', 337, 348, 6),
(125, 'Unnumbered', 347, 348, 6),
(126, 'Unnumbered', 357, 348, 6),
(127, 'Unnumbered', 367, 348, 6),
(128, 'Unnumbered', 377, 348, 6),
(129, 'Unnumbered', 387, 348, 6),
(130, 'Unnumbered', 397, 348, 6),
(131, 'Unnumbered', 407, 348, 6),
(132, 'Unnumbered', 417, 348, 6),
(133, 'Unnumbered', 427, 348, 6),
(134, 'Unnumbered', 437, 348, 6),
(135, 'Unnumbered', 447, 348, 6),
(136, 'Unnumbered', 457, 348, 6),
(137, 'Unnumbered', 207, 358, 6),
(138, 'Unnumbered', 217, 358, 6),
(139, 'Unnumbered', 227, 358, 6),
(140, 'Unnumbered', 237, 358, 6),
(141, 'Unnumbered', 247, 358, 6),
(142, 'Unnumbered', 257, 358, 6),
(143, 'Unnumbered', 267, 358, 6),
(144, 'Unnumbered', 277, 358, 6),
(145, 'Unnumbered', 287, 358, 6),
(146, 'Unnumbered', 297, 358, 6),
(147, 'Unnumbered', 307, 358, 6),
(148, 'Unnumbered', 317, 358, 6),
(149, 'Unnumbered', 327, 358, 6),
(150, 'Unnumbered', 337, 358, 6),
(151, 'Unnumbered', 347, 358, 6),
(152, 'Unnumbered', 357, 358, 6),
(153, 'Unnumbered', 367, 358, 6),
(154, 'Unnumbered', 377, 358, 6),
(155, 'Unnumbered', 387, 358, 6),
(156, 'Unnumbered', 397, 358, 6),
(157, 'Unnumbered', 407, 358, 6),
(158, 'Unnumbered', 417, 358, 6),
(159, 'Unnumbered', 427, 358, 6),
(160, 'Unnumbered', 437, 358, 6),
(161, 'Unnumbered', 447, 358, 6),
(162, 'Unnumbered', 457, 358, 6),
(163, 'Unnumbered', 207, 368, 6),
(164, 'Unnumbered', 217, 368, 6),
(165, 'Unnumbered', 227, 368, 6),
(166, 'Unnumbered', 237, 368, 6),
(167, 'Unnumbered', 247, 368, 6),
(168, 'Unnumbered', 257, 368, 6),
(169, 'Unnumbered', 267, 368, 6),
(170, 'Unnumbered', 277, 368, 6),
(171, 'Unnumbered', 287, 368, 6),
(172, 'Unnumbered', 297, 368, 6),
(173, 'Unnumbered', 307, 368, 6),
(174, 'Unnumbered', 317, 368, 6),
(175, 'Unnumbered', 327, 368, 6),
(176, 'Unnumbered', 337, 368, 6),
(177, 'Unnumbered', 347, 368, 6),
(178, 'Unnumbered', 357, 368, 6),
(179, 'Unnumbered', 367, 368, 6),
(180, 'Unnumbered', 377, 368, 6),
(181, 'Unnumbered', 387, 368, 6),
(182, 'Unnumbered', 397, 368, 6),
(183, 'Unnumbered', 407, 368, 6),
(184, 'Unnumbered', 417, 368, 6),
(185, 'Unnumbered', 427, 368, 6),
(186, 'Unnumbered', 437, 368, 6),
(187, 'Unnumbered', 447, 368, 6),
(188, 'Unnumbered', 457, 368, 6),
(189, 'Unnumbered', 207, 378, 6),
(190, 'Unnumbered', 217, 378, 6),
(191, 'Unnumbered', 227, 378, 6),
(192, 'Unnumbered', 237, 378, 6),
(193, 'Unnumbered', 247, 378, 6),
(194, 'Unnumbered', 257, 378, 6),
(195, 'Unnumbered', 267, 378, 6),
(196, 'Unnumbered', 277, 378, 6),
(197, 'Unnumbered', 287, 378, 6),
(198, 'Unnumbered', 297, 378, 6),
(199, 'Unnumbered', 307, 378, 6),
(200, 'Unnumbered', 317, 378, 6),
(201, 'Unnumbered', 327, 378, 6),
(202, 'Unnumbered', 337, 378, 6),
(203, 'Unnumbered', 347, 378, 6),
(204, 'Unnumbered', 357, 378, 6),
(205, 'Unnumbered', 367, 378, 6),
(206, 'Unnumbered', 377, 378, 6),
(207, 'Unnumbered', 387, 378, 6),
(208, 'Unnumbered', 397, 378, 6),
(209, 'Unnumbered', 407, 378, 6),
(210, 'Unnumbered', 417, 378, 6),
(211, 'Unnumbered', 427, 378, 6),
(212, 'Unnumbered', 437, 378, 6),
(213, 'Unnumbered', 447, 378, 6),
(214, 'Unnumbered', 457, 378, 6),
(215, 'Unnumbered', 207, 388, 6),
(216, 'Unnumbered', 217, 388, 6),
(217, 'Unnumbered', 227, 388, 6),
(218, 'Unnumbered', 237, 388, 6),
(219, 'Unnumbered', 247, 388, 6),
(220, 'Unnumbered', 257, 388, 6),
(221, 'Unnumbered', 267, 388, 6),
(222, 'Unnumbered', 277, 388, 6),
(223, 'Unnumbered', 287, 388, 6),
(224, 'Unnumbered', 297, 388, 6),
(225, 'Unnumbered', 307, 388, 6),
(226, 'Unnumbered', 317, 388, 6),
(227, 'Unnumbered', 327, 388, 6),
(228, 'Unnumbered', 337, 388, 6),
(229, 'Unnumbered', 347, 388, 6),
(230, 'Unnumbered', 357, 388, 6),
(231, 'Unnumbered', 367, 388, 6),
(232, 'Unnumbered', 377, 388, 6),
(233, 'Unnumbered', 387, 388, 6),
(234, 'Unnumbered', 397, 388, 6),
(235, 'Unnumbered', 407, 388, 6),
(236, 'Unnumbered', 417, 388, 6),
(237, 'Unnumbered', 427, 388, 6),
(238, 'Unnumbered', 437, 388, 6),
(239, 'Unnumbered', 447, 388, 6),
(240, 'Unnumbered', 457, 388, 6),
(241, 'Unnumbered', 207, 398, 6),
(242, 'Unnumbered', 217, 398, 6),
(243, 'Unnumbered', 227, 398, 6),
(244, 'Unnumbered', 237, 398, 6),
(245, 'Unnumbered', 247, 398, 6),
(246, 'Unnumbered', 257, 398, 6),
(247, 'Unnumbered', 267, 398, 6),
(248, 'Unnumbered', 277, 398, 6),
(249, 'Unnumbered', 287, 398, 6),
(250, 'Unnumbered', 297, 398, 6),
(251, 'Unnumbered', 307, 398, 6),
(252, 'Unnumbered', 317, 398, 6),
(253, 'Unnumbered', 327, 398, 6),
(254, 'Unnumbered', 337, 398, 6),
(255, 'Unnumbered', 347, 398, 6),
(256, 'Unnumbered', 357, 398, 6),
(257, 'Unnumbered', 367, 398, 6),
(258, 'Unnumbered', 377, 398, 6),
(259, 'Unnumbered', 387, 398, 6),
(260, 'Unnumbered', 397, 398, 6),
(261, 'Unnumbered', 407, 398, 6),
(262, 'Unnumbered', 417, 398, 6),
(263, 'Unnumbered', 427, 398, 6),
(264, 'Unnumbered', 437, 398, 6),
(265, 'Unnumbered', 447, 398, 6),
(266, 'Unnumbered', 457, 398, 6),
(267, 'Unnumbered', 207, 408, 6),
(268, 'Unnumbered', 217, 408, 6),
(269, 'Unnumbered', 227, 408, 6),
(270, 'Unnumbered', 237, 408, 6),
(271, 'Unnumbered', 247, 408, 6),
(272, 'Unnumbered', 257, 408, 6),
(273, 'Unnumbered', 267, 408, 6),
(274, 'Unnumbered', 277, 408, 6),
(275, 'Unnumbered', 287, 408, 6),
(276, 'Unnumbered', 297, 408, 6),
(277, 'Unnumbered', 307, 408, 6),
(278, 'Unnumbered', 317, 408, 6),
(279, 'Unnumbered', 327, 408, 6),
(280, 'Unnumbered', 337, 408, 6),
(281, 'Unnumbered', 347, 408, 6),
(282, 'Unnumbered', 357, 408, 6),
(283, 'Unnumbered', 367, 408, 6),
(284, 'Unnumbered', 377, 408, 6),
(285, 'Unnumbered', 387, 408, 6),
(286, 'Unnumbered', 397, 408, 6),
(287, 'Unnumbered', 407, 408, 6),
(288, 'Unnumbered', 417, 408, 6),
(289, 'Unnumbered', 427, 408, 6),
(290, 'Unnumbered', 437, 408, 6),
(291, 'Unnumbered', 447, 408, 6),
(292, 'Unnumbered', 457, 408, 6),
(293, 'Unnumbered', 207, 418, 6),
(294, 'Unnumbered', 217, 418, 6),
(295, 'Unnumbered', 227, 418, 6),
(296, 'Unnumbered', 237, 418, 6),
(297, 'Unnumbered', 247, 418, 6),
(298, 'Unnumbered', 257, 418, 6),
(299, 'Unnumbered', 267, 418, 6),
(300, 'Unnumbered', 277, 418, 6),
(301, 'Unnumbered', 287, 418, 6),
(302, 'Unnumbered', 297, 418, 6),
(303, 'Unnumbered', 307, 418, 6),
(304, 'Unnumbered', 317, 418, 6),
(305, 'Unnumbered', 327, 418, 6),
(306, 'Unnumbered', 337, 418, 6),
(307, 'Unnumbered', 347, 418, 6),
(308, 'Unnumbered', 357, 418, 6),
(309, 'Unnumbered', 367, 418, 6),
(310, 'Unnumbered', 377, 418, 6),
(311, 'Unnumbered', 387, 418, 6),
(312, 'Unnumbered', 397, 418, 6),
(313, 'Unnumbered', 407, 418, 6),
(314, 'Unnumbered', 417, 418, 6),
(315, 'Unnumbered', 427, 418, 6),
(316, 'Unnumbered', 437, 418, 6),
(317, 'Unnumbered', 447, 418, 6),
(318, 'Unnumbered', 457, 418, 6),
(319, 'Seat 1', 511, 254, 7),
(320, 'Seat 2', 522, 254, 7),
(321, 'Seat 3', 533, 254, 7),
(322, 'Seat 4', 544, 254, 7),
(323, 'Seat 5', 555, 254, 7),
(324, 'Seat 6', 566, 254, 7),
(325, 'Seat 7', 577, 254, 7),
(326, 'Seat 8', 588, 254, 7),
(327, 'Seat 9', 599, 254, 7),
(328, 'Seat 10', 511, 264, 7),
(329, 'Seat 11', 522, 264, 7),
(330, 'Seat 12', 533, 264, 7),
(331, 'Seat 13', 544, 264, 7),
(332, 'Seat 14', 555, 264, 7),
(333, 'Seat 15', 566, 264, 7),
(334, 'Seat 16', 577, 264, 7),
(335, 'Seat 17', 588, 264, 7),
(336, 'Seat 18', 599, 264, 7),
(337, 'Seat 19', 511, 274, 7),
(338, 'Seat 20', 522, 274, 7),
(339, 'Seat 21', 533, 274, 7),
(340, 'Seat 22', 544, 274, 7),
(341, 'Seat 23', 555, 274, 7),
(342, 'Seat 24', 566, 274, 7),
(343, 'Seat 25', 577, 274, 7),
(344, 'Seat 26', 588, 274, 7),
(345, 'Seat 27', 599, 274, 7),
(346, 'Seat 28', 511, 284, 7),
(347, 'Seat 29', 522, 284, 7),
(348, 'Seat 30', 533, 284, 7),
(349, 'Seat 31', 544, 284, 7),
(350, 'Seat 32', 555, 284, 7),
(351, 'Seat 33', 566, 284, 7),
(352, 'Seat 34', 577, 284, 7),
(353, 'Seat 35', 588, 284, 7),
(354, 'Seat 36', 599, 284, 7),
(355, 'Seat 37', 511, 294, 7),
(356, 'Seat 38', 522, 294, 7),
(357, 'Seat 39', 533, 294, 7),
(358, 'Seat 40', 544, 294, 7),
(359, 'Seat 41', 555, 294, 7),
(360, 'Seat 42', 566, 294, 7),
(361, 'Seat 43', 577, 294, 7),
(362, 'Seat 44', 588, 294, 7),
(363, 'Seat 45', 599, 294, 7),
(364, 'Seat 46', 511, 304, 7),
(365, 'Seat 47', 522, 304, 7),
(366, 'Seat 48', 533, 304, 7),
(367, 'Seat 49', 544, 304, 7),
(368, 'Seat 50', 555, 304, 7),
(369, 'Seat 51', 566, 304, 7),
(370, 'Seat 52', 577, 304, 7),
(371, 'Seat 53', 588, 304, 7),
(372, 'Seat 54', 599, 304, 7),
(373, 'Seat 55', 511, 314, 7),
(374, 'Seat 56', 522, 314, 7),
(375, 'Seat 57', 533, 314, 7),
(376, 'Seat 58', 544, 314, 7),
(377, 'Seat 59', 555, 314, 7),
(378, 'Seat 60', 566, 314, 7),
(379, 'Seat 61', 577, 314, 7),
(380, 'Seat 62', 588, 314, 7),
(381, 'Seat 63', 599, 314, 7),
(382, 'Seat 64', 511, 324, 7),
(383, 'Seat 65', 522, 324, 7),
(384, 'Seat 66', 533, 324, 7),
(385, 'Seat 67', 544, 324, 7),
(386, 'Seat 68', 555, 324, 7),
(387, 'Seat 69', 566, 324, 7),
(388, 'Seat 70', 577, 324, 7),
(389, 'Seat 71', 588, 324, 7),
(390, 'Seat 72', 599, 324, 7),
(391, 'Seat 73', 511, 334, 7),
(392, 'Seat 74', 522, 334, 7),
(393, 'Seat 75', 533, 334, 7),
(394, 'Seat 76', 544, 334, 7),
(395, 'Seat 77', 555, 334, 7),
(396, 'Seat 78', 566, 334, 7),
(397, 'Seat 79', 577, 334, 7),
(398, 'Seat 80', 588, 334, 7),
(399, 'Seat 81', 599, 334, 7),
(400, 'Seat 82', 511, 344, 7),
(401, 'Seat 83', 522, 344, 7),
(402, 'Seat 84', 533, 344, 7),
(403, 'Seat 85', 544, 344, 7),
(404, 'Seat 86', 555, 344, 7),
(405, 'Seat 87', 566, 344, 7),
(406, 'Seat 88', 577, 344, 7),
(407, 'Seat 89', 588, 344, 7),
(408, 'Seat 90', 599, 344, 7),
(409, 'Seat 91', 511, 354, 7),
(410, 'Seat 92', 522, 354, 7),
(411, 'Seat 93', 533, 354, 7),
(412, 'Seat 94', 544, 354, 7),
(413, 'Seat 95', 555, 354, 7),
(414, 'Seat 96', 566, 354, 7),
(415, 'Seat 97', 577, 354, 7),
(416, 'Seat 98', 588, 354, 7),
(417, 'Seat 99', 599, 354, 7),
(418, 'Seat 100', 511, 364, 7),
(419, 'Seat 1', 70, 248, 8),
(420, 'Seat 2', 80, 248, 8),
(421, 'Seat 3', 90, 248, 8),
(422, 'Seat 4', 100, 248, 8),
(423, 'Seat 5', 110, 248, 8),
(424, 'Seat 6', 120, 248, 8),
(425, 'Seat 7', 130, 248, 8),
(426, 'Seat 8', 140, 248, 8),
(427, 'Seat 9', 150, 248, 8),
(428, 'Seat 10', 70, 258, 8),
(429, 'Seat 11', 80, 258, 8),
(430, 'Seat 12', 90, 258, 8),
(431, 'Seat 13', 100, 258, 8),
(432, 'Seat 14', 110, 258, 8),
(433, 'Seat 15', 120, 258, 8),
(434, 'Seat 16', 130, 258, 8),
(435, 'Seat 17', 140, 258, 8),
(436, 'Seat 18', 150, 258, 8),
(437, 'Seat 19', 70, 268, 8),
(438, 'Seat 20', 80, 268, 8),
(439, 'Seat 21', 90, 268, 8),
(440, 'Seat 22', 100, 268, 8),
(441, 'Seat 23', 110, 268, 8),
(442, 'Seat 24', 120, 268, 8),
(443, 'Seat 25', 130, 268, 8),
(444, 'Seat 26', 140, 268, 8),
(445, 'Seat 27', 150, 268, 8),
(446, 'Seat 28', 70, 278, 8),
(447, 'Seat 29', 80, 278, 8),
(448, 'Seat 30', 90, 278, 8),
(449, 'Seat 31', 100, 278, 8),
(450, 'Seat 32', 110, 278, 8),
(451, 'Seat 33', 120, 278, 8),
(452, 'Seat 34', 130, 278, 8),
(453, 'Seat 35', 140, 278, 8),
(454, 'Seat 36', 150, 278, 8),
(455, 'Seat 37', 70, 288, 8),
(456, 'Seat 38', 80, 288, 8),
(457, 'Seat 39', 90, 288, 8),
(458, 'Seat 40', 100, 288, 8),
(459, 'Seat 41', 110, 288, 8),
(460, 'Seat 42', 120, 288, 8),
(461, 'Seat 43', 130, 288, 8),
(462, 'Seat 44', 140, 288, 8),
(463, 'Seat 45', 150, 288, 8),
(464, 'Seat 46', 70, 298, 8),
(465, 'Seat 47', 80, 298, 8),
(466, 'Seat 48', 90, 298, 8),
(467, 'Seat 49', 100, 298, 8),
(468, 'Seat 50', 110, 298, 8),
(469, 'Seat 51', 120, 298, 8),
(470, 'Seat 52', 130, 298, 8),
(471, 'Seat 53', 140, 298, 8),
(472, 'Seat 54', 150, 298, 8),
(473, 'Seat 55', 70, 308, 8),
(474, 'Seat 56', 80, 308, 8),
(475, 'Seat 57', 90, 308, 8),
(476, 'Seat 58', 100, 308, 8),
(477, 'Seat 59', 110, 308, 8),
(478, 'Seat 60', 120, 308, 8),
(479, 'Seat 61', 130, 308, 8),
(480, 'Seat 62', 140, 308, 8),
(481, 'Seat 63', 150, 308, 8),
(482, 'Seat 64', 70, 318, 8),
(483, 'Seat 65', 80, 318, 8),
(484, 'Seat 66', 90, 318, 8),
(485, 'Seat 67', 100, 318, 8),
(486, 'Seat 68', 110, 318, 8),
(487, 'Seat 69', 120, 318, 8),
(488, 'Seat 70', 130, 318, 8),
(489, 'Seat 71', 140, 318, 8),
(490, 'Seat 72', 150, 318, 8),
(491, 'Seat 73', 70, 328, 8),
(492, 'Seat 74', 80, 328, 8),
(493, 'Seat 75', 90, 328, 8),
(494, 'Seat 76', 100, 328, 8),
(495, 'Seat 77', 110, 328, 8),
(496, 'Seat 78', 120, 328, 8),
(497, 'Seat 79', 130, 328, 8),
(498, 'Seat 80', 140, 328, 8),
(499, 'Seat 81', 150, 328, 8),
(500, 'Seat 82', 70, 338, 8),
(501, 'Seat 83', 80, 338, 8),
(502, 'Seat 84', 90, 338, 8),
(503, 'Seat 85', 100, 338, 8),
(504, 'Seat 86', 110, 338, 8),
(505, 'Seat 87', 120, 338, 8),
(506, 'Seat 88', 130, 338, 8),
(507, 'Seat 89', 140, 338, 8),
(508, 'Seat 90', 150, 338, 8),
(509, 'Seat 91', 70, 348, 8),
(510, 'Seat 92', 80, 348, 8),
(511, 'Seat 93', 90, 348, 8),
(512, 'Seat 94', 100, 348, 8),
(513, 'Seat 95', 110, 348, 8),
(514, 'Seat 96', 120, 348, 8),
(515, 'Seat 97', 130, 348, 8),
(516, 'Seat 98', 140, 348, 8),
(517, 'Seat 99', 150, 348, 8),
(518, 'Seat 100', 70, 358, 8);

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
(4, 'Sector 2', 200, 147, 415, 210, 6, 0),
(5, 'Stage', 191, 164, 481, 263, 7, 1),
(6, 'Parterre', 192, 293, 480, 434, 7, 0),
(7, 'Right Wing', 496, 239, 615, 397, 7, 0),
(8, 'Left Wing', 55, 233, 167, 387, 7, 0);

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
(29, 30.00, 1, 6, 20, 4),
(30, 30.00, 0, 6, 20, 5),
(31, 30.00, 0, NULL, 20, 6),
(38, 30.00, 0, NULL, 24, 1),
(39, 30.00, 0, NULL, 24, 2),
(40, 30.00, 0, NULL, 24, 3),
(50, 30.00, 0, NULL, 28, 7),
(51, 30.00, 0, NULL, 28, 8),
(52, 30.00, 0, NULL, 28, 9),
(53, 30.00, 0, NULL, 28, 10),
(54, 30.00, 0, NULL, 28, 11),
(55, 30.00, 0, NULL, 28, 12),
(56, 30.00, 0, NULL, 28, 13),
(57, 30.00, 0, NULL, 28, 14),
(58, 30.00, 0, NULL, 28, 15),
(59, 30.00, 0, NULL, 28, 16),
(60, 30.00, 0, NULL, 28, 17),
(61, 30.00, 0, NULL, 28, 18),
(62, 30.00, 0, NULL, 28, 19),
(63, 30.00, 0, NULL, 28, 20),
(64, 30.00, 0, NULL, 28, 21),
(65, 30.00, 0, NULL, 28, 22),
(66, 30.00, 0, NULL, 28, 23),
(67, 30.00, 0, 6, 28, 24),
(68, 30.00, 0, NULL, 28, 25),
(69, 30.00, 0, NULL, 28, 26),
(70, 30.00, 0, NULL, 28, 27),
(71, 30.00, 0, NULL, 28, 28),
(72, 30.00, 0, NULL, 28, 29),
(73, 30.00, 0, NULL, 28, 30),
(74, 30.00, 0, NULL, 28, 31),
(75, 30.00, 0, NULL, 28, 32),
(76, 30.00, 0, NULL, 28, 33),
(77, 30.00, 0, NULL, 28, 34),
(78, 30.00, 0, NULL, 28, 35),
(79, 30.00, 0, NULL, 28, 36),
(80, 30.00, 0, NULL, 28, 37),
(81, 30.00, 0, NULL, 28, 38),
(82, 30.00, 0, NULL, 28, 39),
(83, 30.00, 0, NULL, 28, 40),
(84, 30.00, 0, NULL, 28, 41),
(85, 30.00, 0, NULL, 28, 42),
(86, 30.00, 0, NULL, 28, 43),
(87, 30.00, 0, NULL, 28, 44),
(88, 30.00, 0, NULL, 28, 45),
(89, 30.00, 0, NULL, 28, 46),
(90, 30.00, 0, NULL, 28, 47),
(91, 30.00, 0, NULL, 28, 48),
(92, 30.00, 0, NULL, 28, 49),
(93, 30.00, 0, NULL, 28, 50),
(94, 30.00, 0, NULL, 28, 51),
(95, 30.00, 0, NULL, 28, 52),
(96, 30.00, 0, NULL, 28, 53),
(97, 30.00, 0, NULL, 28, 54),
(98, 30.00, 0, NULL, 28, 55),
(99, 30.00, 0, NULL, 28, 56),
(100, 30.00, 0, NULL, 28, 57),
(101, 30.00, 0, NULL, 28, 58),
(102, 30.00, 0, NULL, 28, 59),
(103, 30.00, 0, NULL, 28, 60),
(104, 30.00, 0, NULL, 28, 61),
(105, 30.00, 0, NULL, 28, 62),
(106, 30.00, 0, NULL, 28, 63),
(107, 30.00, 0, NULL, 28, 64),
(108, 30.00, 0, NULL, 28, 65),
(109, 30.00, 0, NULL, 28, 66),
(110, 30.00, 0, NULL, 28, 67),
(111, 30.00, 0, NULL, 28, 68),
(112, 30.00, 0, NULL, 28, 69),
(113, 30.00, 0, NULL, 28, 70),
(114, 30.00, 0, NULL, 28, 71),
(115, 30.00, 0, NULL, 28, 72),
(116, 30.00, 0, NULL, 28, 73),
(117, 30.00, 0, NULL, 28, 74),
(118, 30.00, 0, NULL, 28, 75),
(119, 30.00, 0, NULL, 28, 76),
(120, 30.00, 0, NULL, 28, 77),
(121, 30.00, 0, NULL, 28, 78),
(122, 30.00, 0, NULL, 28, 79),
(123, 30.00, 0, NULL, 28, 80),
(124, 30.00, 0, NULL, 28, 81),
(125, 30.00, 0, NULL, 28, 82),
(126, 30.00, 0, NULL, 28, 83),
(127, 30.00, 0, NULL, 28, 84),
(128, 30.00, 0, NULL, 28, 85),
(129, 30.00, 0, NULL, 28, 86),
(130, 30.00, 0, NULL, 28, 87),
(131, 30.00, 0, NULL, 28, 88),
(132, 30.00, 0, NULL, 28, 89),
(133, 30.00, 0, NULL, 28, 90),
(134, 30.00, 0, NULL, 28, 91),
(135, 30.00, 0, NULL, 28, 92),
(136, 30.00, 0, NULL, 28, 93),
(137, 30.00, 0, NULL, 28, 94),
(138, 30.00, 0, NULL, 28, 95),
(139, 30.00, 0, NULL, 28, 96),
(140, 30.00, 0, NULL, 28, 97),
(141, 30.00, 0, NULL, 28, 98),
(142, 30.00, 0, NULL, 28, 99),
(143, 30.00, 0, NULL, 28, 100),
(144, 30.00, 0, NULL, 28, 101),
(145, 30.00, 0, NULL, 28, 102),
(146, 30.00, 0, NULL, 28, 103),
(147, 30.00, 0, NULL, 28, 104),
(148, 30.00, 0, NULL, 28, 105),
(149, 30.00, 0, NULL, 28, 106),
(150, 30.00, 0, NULL, 28, 107),
(151, 30.00, 0, NULL, 28, 108),
(152, 30.00, 0, NULL, 28, 109),
(153, 30.00, 0, NULL, 28, 110),
(154, 30.00, 0, NULL, 28, 111),
(155, 30.00, 0, NULL, 28, 112),
(156, 30.00, 0, NULL, 28, 113),
(157, 30.00, 0, NULL, 28, 114),
(158, 30.00, 0, NULL, 28, 115),
(159, 30.00, 0, NULL, 28, 116),
(160, 30.00, 0, NULL, 28, 117),
(161, 30.00, 0, NULL, 28, 118),
(162, 30.00, 0, NULL, 28, 119),
(163, 30.00, 0, NULL, 28, 120),
(164, 30.00, 0, NULL, 28, 121),
(165, 30.00, 0, NULL, 28, 122),
(166, 30.00, 0, NULL, 28, 123),
(167, 30.00, 0, NULL, 28, 124),
(168, 30.00, 0, NULL, 28, 125),
(169, 30.00, 0, NULL, 28, 126),
(170, 30.00, 0, NULL, 28, 127),
(171, 30.00, 0, NULL, 28, 128),
(172, 30.00, 0, NULL, 28, 129),
(173, 30.00, 0, NULL, 28, 130),
(174, 30.00, 0, NULL, 28, 131),
(175, 30.00, 0, NULL, 28, 132),
(176, 30.00, 0, NULL, 28, 133),
(177, 30.00, 0, NULL, 28, 134),
(178, 30.00, 0, NULL, 28, 135),
(179, 30.00, 0, NULL, 28, 136),
(180, 30.00, 0, NULL, 28, 137),
(181, 30.00, 0, NULL, 28, 138),
(182, 30.00, 0, NULL, 28, 139),
(183, 30.00, 0, NULL, 28, 140),
(184, 30.00, 0, NULL, 28, 141),
(185, 30.00, 0, NULL, 28, 142),
(186, 30.00, 0, NULL, 28, 143),
(187, 30.00, 0, NULL, 28, 144),
(188, 30.00, 0, NULL, 28, 145),
(189, 30.00, 0, NULL, 28, 146),
(190, 30.00, 0, NULL, 28, 147),
(191, 30.00, 0, NULL, 28, 148),
(192, 30.00, 0, NULL, 28, 149),
(193, 30.00, 0, NULL, 28, 150),
(194, 30.00, 0, NULL, 28, 151),
(195, 30.00, 0, NULL, 28, 152),
(196, 30.00, 0, NULL, 28, 153),
(197, 30.00, 0, NULL, 28, 154),
(198, 30.00, 0, NULL, 28, 155),
(199, 30.00, 0, NULL, 28, 156),
(200, 30.00, 0, NULL, 28, 157),
(201, 30.00, 0, NULL, 28, 158),
(202, 30.00, 0, NULL, 28, 159),
(203, 30.00, 0, NULL, 28, 160),
(204, 30.00, 0, NULL, 28, 161),
(205, 30.00, 0, NULL, 28, 162),
(206, 30.00, 0, NULL, 28, 163),
(207, 30.00, 0, NULL, 28, 164),
(208, 30.00, 0, NULL, 28, 165),
(209, 30.00, 0, NULL, 28, 166),
(210, 30.00, 0, NULL, 28, 167),
(211, 30.00, 0, NULL, 28, 168),
(212, 30.00, 0, NULL, 28, 169),
(213, 30.00, 0, NULL, 28, 170),
(214, 30.00, 0, NULL, 28, 171),
(215, 30.00, 0, NULL, 28, 172),
(216, 30.00, 0, NULL, 28, 173),
(217, 30.00, 0, NULL, 28, 174),
(218, 30.00, 0, NULL, 28, 175),
(219, 30.00, 0, NULL, 28, 176),
(220, 30.00, 0, NULL, 28, 177),
(221, 30.00, 0, NULL, 28, 178),
(222, 30.00, 0, NULL, 28, 179),
(223, 30.00, 0, NULL, 28, 180),
(224, 30.00, 0, NULL, 28, 181),
(225, 30.00, 0, NULL, 28, 182),
(226, 30.00, 0, NULL, 28, 183),
(227, 30.00, 0, NULL, 28, 184),
(228, 30.00, 0, NULL, 28, 185),
(229, 30.00, 0, NULL, 28, 186),
(230, 30.00, 0, NULL, 28, 187),
(231, 30.00, 0, NULL, 28, 188),
(232, 30.00, 0, NULL, 28, 189),
(233, 30.00, 0, NULL, 28, 190),
(234, 30.00, 0, NULL, 28, 191),
(235, 30.00, 0, NULL, 28, 192),
(236, 30.00, 0, NULL, 28, 193),
(237, 30.00, 0, NULL, 28, 194),
(238, 30.00, 0, NULL, 28, 195),
(239, 30.00, 0, NULL, 28, 196),
(240, 30.00, 0, NULL, 28, 197),
(241, 30.00, 0, NULL, 28, 198),
(242, 30.00, 0, NULL, 28, 199),
(243, 30.00, 0, NULL, 28, 200),
(244, 30.00, 0, NULL, 28, 201),
(245, 30.00, 0, NULL, 28, 202),
(246, 30.00, 0, NULL, 28, 203),
(247, 30.00, 0, NULL, 28, 204),
(248, 30.00, 0, NULL, 28, 205),
(249, 30.00, 0, NULL, 28, 206),
(250, 30.00, 0, NULL, 28, 207),
(251, 30.00, 0, NULL, 28, 208),
(252, 30.00, 0, NULL, 28, 209),
(253, 30.00, 0, NULL, 28, 210),
(254, 30.00, 0, NULL, 28, 211),
(255, 30.00, 0, NULL, 28, 212),
(256, 30.00, 0, NULL, 28, 213),
(257, 30.00, 0, NULL, 28, 214),
(258, 30.00, 0, NULL, 28, 215),
(259, 30.00, 0, NULL, 28, 216),
(260, 30.00, 0, NULL, 28, 217),
(261, 30.00, 0, NULL, 28, 218),
(262, 30.00, 0, NULL, 28, 219),
(263, 30.00, 0, NULL, 28, 220),
(264, 30.00, 0, NULL, 28, 221),
(265, 30.00, 0, NULL, 28, 222),
(266, 30.00, 0, NULL, 28, 223),
(267, 30.00, 0, NULL, 28, 224),
(268, 30.00, 0, NULL, 28, 225),
(269, 30.00, 0, NULL, 28, 226),
(270, 30.00, 0, NULL, 28, 227),
(271, 30.00, 0, NULL, 28, 228),
(272, 30.00, 0, NULL, 28, 229),
(273, 30.00, 0, NULL, 28, 230),
(274, 30.00, 0, NULL, 28, 231),
(275, 30.00, 0, NULL, 28, 232),
(276, 30.00, 0, NULL, 28, 233),
(277, 30.00, 0, NULL, 28, 234),
(278, 30.00, 0, NULL, 28, 235),
(279, 30.00, 0, NULL, 28, 236),
(280, 30.00, 0, NULL, 28, 237),
(281, 30.00, 0, NULL, 28, 238),
(282, 30.00, 0, NULL, 28, 239),
(283, 30.00, 0, NULL, 28, 240),
(284, 30.00, 0, NULL, 28, 241),
(285, 30.00, 0, NULL, 28, 242),
(286, 30.00, 0, NULL, 28, 243),
(287, 30.00, 0, NULL, 28, 244),
(288, 30.00, 0, NULL, 28, 245),
(289, 30.00, 0, NULL, 28, 246),
(290, 30.00, 0, NULL, 28, 247),
(291, 30.00, 0, NULL, 28, 248),
(292, 30.00, 0, NULL, 28, 249),
(293, 30.00, 0, NULL, 28, 250),
(294, 30.00, 0, NULL, 28, 251),
(295, 30.00, 0, NULL, 28, 252),
(296, 30.00, 0, NULL, 28, 253),
(297, 30.00, 0, NULL, 28, 254),
(298, 30.00, 0, NULL, 28, 255),
(299, 30.00, 0, NULL, 28, 256),
(300, 30.00, 0, NULL, 28, 257),
(301, 30.00, 0, NULL, 28, 258),
(302, 30.00, 0, NULL, 28, 259),
(303, 30.00, 0, NULL, 28, 260),
(304, 30.00, 0, NULL, 28, 261),
(305, 30.00, 0, NULL, 28, 262),
(306, 30.00, 0, NULL, 28, 263),
(307, 30.00, 0, NULL, 28, 264),
(308, 30.00, 0, NULL, 28, 265),
(309, 30.00, 0, NULL, 28, 266),
(310, 30.00, 0, NULL, 28, 267),
(311, 30.00, 0, NULL, 28, 268),
(312, 30.00, 0, NULL, 28, 269),
(313, 30.00, 0, NULL, 28, 270),
(314, 30.00, 0, NULL, 28, 271),
(315, 30.00, 0, NULL, 28, 272),
(316, 30.00, 0, NULL, 28, 273),
(317, 30.00, 0, NULL, 28, 274),
(318, 30.00, 0, NULL, 28, 275),
(319, 30.00, 0, NULL, 28, 276),
(320, 30.00, 0, NULL, 28, 277),
(321, 30.00, 0, NULL, 28, 278),
(322, 30.00, 0, NULL, 28, 279),
(323, 30.00, 0, NULL, 28, 280),
(324, 30.00, 0, NULL, 28, 281),
(325, 30.00, 0, NULL, 28, 282),
(326, 30.00, 0, NULL, 28, 283),
(327, 30.00, 0, NULL, 28, 284),
(328, 30.00, 0, NULL, 28, 285),
(329, 30.00, 0, NULL, 28, 286),
(330, 30.00, 0, NULL, 28, 287),
(331, 30.00, 0, NULL, 28, 288),
(332, 30.00, 0, NULL, 28, 289),
(333, 30.00, 0, NULL, 28, 290),
(334, 30.00, 0, NULL, 28, 291),
(335, 30.00, 0, NULL, 28, 292),
(336, 30.00, 0, NULL, 28, 293),
(337, 30.00, 0, NULL, 28, 294),
(338, 30.00, 0, NULL, 28, 295),
(339, 30.00, 0, NULL, 28, 296),
(340, 30.00, 0, NULL, 28, 297),
(341, 30.00, 0, NULL, 28, 298),
(342, 30.00, 0, NULL, 28, 299),
(343, 30.00, 0, NULL, 28, 300),
(344, 30.00, 0, NULL, 28, 301),
(345, 30.00, 0, NULL, 28, 302),
(346, 30.00, 0, NULL, 28, 303),
(347, 30.00, 0, NULL, 28, 304),
(348, 30.00, 0, NULL, 28, 305),
(349, 30.00, 0, NULL, 28, 306),
(350, 30.00, 0, NULL, 28, 307),
(351, 30.00, 0, NULL, 28, 308),
(352, 30.00, 0, NULL, 28, 309),
(353, 30.00, 0, NULL, 28, 310),
(354, 30.00, 0, NULL, 28, 311),
(355, 30.00, 0, NULL, 28, 312),
(356, 30.00, 0, NULL, 28, 313),
(357, 30.00, 0, NULL, 28, 314),
(358, 30.00, 0, NULL, 28, 315),
(359, 30.00, 0, NULL, 28, 316),
(360, 30.00, 0, NULL, 28, 317),
(361, 30.00, 0, NULL, 28, 318),
(362, 25.00, 0, NULL, 28, 319),
(363, 25.00, 0, NULL, 28, 320),
(364, 25.00, 0, NULL, 28, 321),
(365, 25.00, 0, NULL, 28, 322),
(366, 25.00, 0, NULL, 28, 323),
(367, 25.00, 0, NULL, 28, 324),
(368, 25.00, 0, NULL, 28, 325),
(369, 25.00, 0, NULL, 28, 326),
(370, 25.00, 0, NULL, 28, 327),
(371, 25.00, 0, NULL, 28, 328),
(372, 25.00, 0, NULL, 28, 329),
(373, 25.00, 0, NULL, 28, 330),
(374, 25.00, 0, NULL, 28, 331),
(375, 25.00, 0, NULL, 28, 332),
(376, 25.00, 0, NULL, 28, 333),
(377, 25.00, 0, NULL, 28, 334),
(378, 25.00, 0, NULL, 28, 335),
(379, 25.00, 0, NULL, 28, 336),
(380, 25.00, 0, NULL, 28, 337),
(381, 25.00, 0, NULL, 28, 338),
(382, 25.00, 0, NULL, 28, 339),
(383, 25.00, 0, NULL, 28, 340),
(384, 25.00, 0, NULL, 28, 341),
(385, 25.00, 0, NULL, 28, 342),
(386, 25.00, 0, NULL, 28, 343),
(387, 25.00, 0, NULL, 28, 344),
(388, 25.00, 0, NULL, 28, 345),
(389, 25.00, 0, NULL, 28, 346),
(390, 25.00, 0, NULL, 28, 347),
(391, 25.00, 0, NULL, 28, 348),
(392, 25.00, 0, NULL, 28, 349),
(393, 25.00, 0, NULL, 28, 350),
(394, 25.00, 0, NULL, 28, 351),
(395, 25.00, 0, NULL, 28, 352),
(396, 25.00, 0, NULL, 28, 353),
(397, 25.00, 0, NULL, 28, 354),
(398, 25.00, 0, NULL, 28, 355),
(399, 25.00, 0, NULL, 28, 356),
(400, 25.00, 0, NULL, 28, 357),
(401, 25.00, 0, NULL, 28, 358),
(402, 25.00, 0, NULL, 28, 359),
(403, 25.00, 0, NULL, 28, 360),
(404, 25.00, 0, NULL, 28, 361),
(405, 25.00, 0, NULL, 28, 362),
(406, 25.00, 0, NULL, 28, 363),
(407, 25.00, 0, NULL, 28, 364),
(408, 25.00, 0, NULL, 28, 365),
(409, 25.00, 0, NULL, 28, 366),
(410, 25.00, 0, NULL, 28, 367),
(411, 25.00, 0, NULL, 28, 368),
(412, 25.00, 0, NULL, 28, 369),
(413, 25.00, 0, NULL, 28, 370),
(414, 25.00, 0, NULL, 28, 371),
(415, 25.00, 0, NULL, 28, 372),
(416, 25.00, 0, NULL, 28, 373),
(417, 25.00, 0, NULL, 28, 374),
(418, 25.00, 0, NULL, 28, 375),
(419, 25.00, 0, NULL, 28, 376),
(420, 25.00, 0, NULL, 28, 377),
(421, 25.00, 0, NULL, 28, 378),
(422, 25.00, 0, NULL, 28, 379),
(423, 25.00, 0, NULL, 28, 380),
(424, 25.00, 0, NULL, 28, 381),
(425, 25.00, 0, NULL, 28, 382),
(426, 25.00, 0, NULL, 28, 383),
(427, 25.00, 0, NULL, 28, 384),
(428, 25.00, 0, NULL, 28, 385),
(429, 25.00, 0, NULL, 28, 386),
(430, 25.00, 0, NULL, 28, 387),
(431, 25.00, 0, NULL, 28, 388),
(432, 25.00, 0, NULL, 28, 389),
(433, 25.00, 0, NULL, 28, 390),
(434, 25.00, 0, NULL, 28, 391),
(435, 25.00, 0, NULL, 28, 392),
(436, 25.00, 0, NULL, 28, 393),
(437, 25.00, 0, NULL, 28, 394),
(438, 25.00, 0, NULL, 28, 395),
(439, 25.00, 0, NULL, 28, 396),
(440, 25.00, 0, NULL, 28, 397),
(441, 25.00, 0, NULL, 28, 398),
(442, 25.00, 0, NULL, 28, 399),
(443, 25.00, 0, NULL, 28, 400),
(444, 25.00, 0, NULL, 28, 401),
(445, 25.00, 0, NULL, 28, 402),
(446, 25.00, 0, NULL, 28, 403),
(447, 25.00, 0, NULL, 28, 404),
(448, 25.00, 0, NULL, 28, 405),
(449, 25.00, 0, NULL, 28, 406),
(450, 25.00, 0, NULL, 28, 407),
(451, 25.00, 0, NULL, 28, 408),
(452, 25.00, 0, NULL, 28, 409),
(453, 25.00, 0, NULL, 28, 410),
(454, 25.00, 0, NULL, 28, 411),
(455, 25.00, 0, NULL, 28, 412),
(456, 25.00, 0, NULL, 28, 413),
(457, 25.00, 0, NULL, 28, 414),
(458, 25.00, 0, NULL, 28, 415),
(459, 25.00, 0, NULL, 28, 416),
(460, 25.00, 0, NULL, 28, 417),
(461, 25.00, 0, NULL, 28, 418),
(462, 20.00, 0, NULL, 28, 419),
(463, 20.00, 0, NULL, 28, 420),
(464, 20.00, 0, NULL, 28, 421),
(465, 20.00, 0, NULL, 28, 422),
(466, 20.00, 0, NULL, 28, 423),
(467, 20.00, 0, NULL, 28, 424),
(468, 20.00, 0, NULL, 28, 425),
(469, 20.00, 0, NULL, 28, 426),
(470, 20.00, 0, 6, 28, 427),
(471, 20.00, 0, NULL, 28, 428),
(472, 20.00, 0, NULL, 28, 429),
(473, 20.00, 0, NULL, 28, 430),
(474, 20.00, 0, NULL, 28, 431),
(475, 20.00, 0, NULL, 28, 432),
(476, 20.00, 0, NULL, 28, 433),
(477, 20.00, 0, NULL, 28, 434),
(478, 20.00, 0, NULL, 28, 435),
(479, 20.00, 0, 6, 28, 436),
(480, 20.00, 0, NULL, 28, 437),
(481, 20.00, 0, NULL, 28, 438),
(482, 20.00, 0, NULL, 28, 439),
(483, 20.00, 0, NULL, 28, 440),
(484, 20.00, 0, NULL, 28, 441),
(485, 20.00, 0, NULL, 28, 442),
(486, 20.00, 0, NULL, 28, 443),
(487, 20.00, 0, NULL, 28, 444),
(488, 20.00, 0, 6, 28, 445),
(489, 20.00, 0, NULL, 28, 446),
(490, 20.00, 0, NULL, 28, 447),
(491, 20.00, 0, NULL, 28, 448),
(492, 20.00, 0, NULL, 28, 449),
(493, 20.00, 0, NULL, 28, 450),
(494, 20.00, 0, NULL, 28, 451),
(495, 20.00, 0, NULL, 28, 452),
(496, 20.00, 0, NULL, 28, 453),
(497, 20.00, 0, NULL, 28, 454),
(498, 20.00, 0, NULL, 28, 455),
(499, 20.00, 0, NULL, 28, 456),
(500, 20.00, 0, NULL, 28, 457),
(501, 20.00, 0, NULL, 28, 458),
(502, 20.00, 0, NULL, 28, 459),
(503, 20.00, 0, NULL, 28, 460),
(504, 20.00, 0, NULL, 28, 461),
(505, 20.00, 0, NULL, 28, 462),
(506, 20.00, 0, NULL, 28, 463),
(507, 20.00, 0, NULL, 28, 464),
(508, 20.00, 0, NULL, 28, 465),
(509, 20.00, 0, NULL, 28, 466),
(510, 20.00, 0, NULL, 28, 467),
(511, 20.00, 0, NULL, 28, 468),
(512, 20.00, 0, NULL, 28, 469),
(513, 20.00, 0, NULL, 28, 470),
(514, 20.00, 0, NULL, 28, 471),
(515, 20.00, 0, NULL, 28, 472),
(516, 20.00, 0, NULL, 28, 473),
(517, 20.00, 0, NULL, 28, 474),
(518, 20.00, 0, NULL, 28, 475),
(519, 20.00, 0, NULL, 28, 476),
(520, 20.00, 0, NULL, 28, 477),
(521, 20.00, 0, NULL, 28, 478),
(522, 20.00, 0, NULL, 28, 479),
(523, 20.00, 0, NULL, 28, 480),
(524, 20.00, 0, NULL, 28, 481),
(525, 20.00, 0, NULL, 28, 482),
(526, 20.00, 0, NULL, 28, 483),
(527, 20.00, 0, NULL, 28, 484),
(528, 20.00, 0, NULL, 28, 485),
(529, 20.00, 0, NULL, 28, 486),
(530, 20.00, 0, NULL, 28, 487),
(531, 20.00, 0, NULL, 28, 488),
(532, 20.00, 0, NULL, 28, 489),
(533, 20.00, 0, NULL, 28, 490),
(534, 20.00, 0, NULL, 28, 491),
(535, 20.00, 0, NULL, 28, 492),
(536, 20.00, 0, NULL, 28, 493),
(537, 20.00, 0, NULL, 28, 494),
(538, 20.00, 0, NULL, 28, 495),
(539, 20.00, 0, NULL, 28, 496),
(540, 20.00, 0, NULL, 28, 497),
(541, 20.00, 0, NULL, 28, 498),
(542, 20.00, 0, NULL, 28, 499),
(543, 20.00, 0, NULL, 28, 500),
(544, 20.00, 0, NULL, 28, 501),
(545, 20.00, 0, NULL, 28, 502),
(546, 20.00, 0, NULL, 28, 503),
(547, 20.00, 0, NULL, 28, 504),
(548, 20.00, 0, NULL, 28, 505),
(549, 20.00, 0, NULL, 28, 506),
(550, 20.00, 0, NULL, 28, 507),
(551, 20.00, 0, NULL, 28, 508),
(552, 20.00, 0, NULL, 28, 509),
(553, 20.00, 0, NULL, 28, 510),
(554, 20.00, 0, NULL, 28, 511),
(555, 20.00, 0, NULL, 28, 512),
(556, 20.00, 0, NULL, 28, 513),
(557, 20.00, 0, NULL, 28, 514),
(558, 20.00, 0, NULL, 28, 515),
(559, 20.00, 0, NULL, 28, 516),
(560, 20.00, 0, NULL, 28, 517),
(561, 20.00, 0, NULL, 28, 518);

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
(4, 'Prova Nayt', 'tour.jpeg', 4);

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
  `session_token` varchar(255) DEFAULT NULL,
  `fcm_token` varchar(255) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dump dei dati per la tabella `user`
--

INSERT INTO `user` (`id`, `username`, `email`, `password`, `name`, `surname`, `birthdate`, `image`, `refunds`, `session_token`, `fcm_token`) VALUES
(6, 'ciccio1', 'ciccio@example.com', '$2b$12$I4Ie4q39.iO2kk.ILEW10Oa2OMS7pZC3yYuA7UHR5FnUBD/jUULBC', 'Ciccio', 'Pippo', '2003-05-18', 'ciccio1.jpg', 0.00, 'eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJ1c2VyX2lkIjo2fQ.aR2HUmYnDNH0JP0s1touqOiMEv5D7YN6Re9LTzpEalU', NULL),
(11, 'monta', 'elena.montalti@gmail.com', '$2b$12$Juf5yZrzS9.slbZoOfF0buhfdVCW3sOZzVPWo/8DxN3yIAC2xE3h.', 'elena', 'montalti', '2003-10-13', NULL, 0.00, NULL, NULL),
(14, 'todel', 'todel@gmail.com', '$2b$12$tG02kipLgeyStOKCrijnzeIuB894BDGuTtlZty3o4EevQ7DZiomG.', 'todel', 'todel', '1988-05-18', NULL, 0.00, NULL, NULL);

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
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=5;

--
-- AUTO_INCREMENT per la tabella `artist_concert`
--
ALTER TABLE `artist_concert`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=36;

--
-- AUTO_INCREMENT per la tabella `concert`
--
ALTER TABLE `concert`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=29;

--
-- AUTO_INCREMENT per la tabella `likes`
--
ALTER TABLE `likes`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=17;

--
-- AUTO_INCREMENT per la tabella `notification`
--
ALTER TABLE `notification`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=29;

--
-- AUTO_INCREMENT per la tabella `place`
--
ALTER TABLE `place`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=8;

--
-- AUTO_INCREMENT per la tabella `record_company`
--
ALTER TABLE `record_company`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=5;

--
-- AUTO_INCREMENT per la tabella `review`
--
ALTER TABLE `review`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=14;

--
-- AUTO_INCREMENT per la tabella `seat`
--
ALTER TABLE `seat`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=519;

--
-- AUTO_INCREMENT per la tabella `sector`
--
ALTER TABLE `sector`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=9;

--
-- AUTO_INCREMENT per la tabella `ticket`
--
ALTER TABLE `ticket`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=562;

--
-- AUTO_INCREMENT per la tabella `tour`
--
ALTER TABLE `tour`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=7;

--
-- AUTO_INCREMENT per la tabella `user`
--
ALTER TABLE `user`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=15;

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
