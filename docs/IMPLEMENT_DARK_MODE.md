<!DOCTYPE html>
<html lang="en">
<head>
  <meta name="title" content="Edward & Clara | June 20, 2026">
  <meta name="description" content="You are cordially invited to celebrate our wedding.">
  <meta property="og:type" content="website">
  <meta property="og:url" content="https://dardoblack02.github.io/ed-and-clara/">
  <meta property="og:title" content="Edward & Clara | June 20, 2026">
  <meta property="og:description" content="We can't wait to celebrate our special day with you!">
  <meta property="og:image" content="https://dardoblack02.github.io/ed-and-clara/Thumbnail.png">
  <meta charset="UTF-8">
  <meta name="viewport" content="width=device-width, initial-scale=1.0, maximum-scale=1.0, user-scalable=no">
  <title>Edward & Clara | June 20, 2026</title>
  <link href="https://fonts.googleapis.com/css2?family=Cormorant+Garamond:ital,wght@0,300;0,400;0,600;1,300;1,400&family=DM+Sans:wght@300;400;500&display=swap" rel="stylesheet">

  <style>
    :root {
      --burgundy: #6B0F2B;
      --burgundy-soft: #8B1A3A;
      --gold: #B8913F;
      --gold-light: #D4B06A;
      --gold-pale: #F0E4C8;
      --ivory: #FAF7F2;
      --ivory-dark: #F2EDE4;
      --white: #ffffff;
      --ink: #1E0E14;
      --ink-soft: #4A2E38;
      --muted: #9A8080;
      --border: rgba(184,145,63,0.2);
      --border-strong: rgba(184,145,63,0.45);
    }

    *, *::before, *::after { box-sizing: border-box; margin: 0; padding: 0; }

    html { scroll-behavior: smooth; }

    body {
      font-family: 'DM Sans', sans-serif;
      background: var(--ivory);
      color: var(--ink);
      overflow-x: hidden;
      -webkit-font-smoothing: antialiased;
    }

    h1, h2, h3, h4 { font-family: 'Cormorant Garamond', serif; font-weight: 400; }

    /* ─── FADE IN ANIMATION ─── */
    .reveal {
      opacity: 0;
      transform: translateY(28px);
      transition: opacity 0.75s ease, transform 0.75s ease;
    }
    .reveal.visible {
      opacity: 1;
      transform: translateY(0);
    }

    /* ─── ORNAMENT DIVIDER ─── */
    .ornament {
      display: flex;
      align-items: center;
      gap: 14px;
      justify-content: center;
      margin: 0 auto 18px;
      width: fit-content;
    }
    .ornament::before, .ornament::after {
      content: '';
      width: 60px;
      height: 1px;
      background: var(--gold);
      opacity: 0.6;
    }
    .ornament-diamond {
      width: 6px; height: 6px;
      background: var(--gold);
      transform: rotate(45deg);
      flex-shrink: 0;
    }

    /* ─── FLOATING NAV ─── */
    .floating-nav {
      position: fixed;
      top: 18px;
      left: 50%;
      transform: translateX(-50%);
      z-index: 9999;
      background: rgba(250,247,242,0.55);
      backdrop-filter: blur(20px);
      -webkit-backdrop-filter: blur(20px);
      border: 1px solid var(--border-strong);
      border-radius: 50px;
      display: flex;
      align-items: center;
      gap: 0;
      box-shadow: 0 8px 32px rgba(107,15,43,0.12), 0 2px 8px rgba(0,0,0,0.06);
      max-width: 95vw;
      overflow-x: auto;
      scrollbar-width: none;
      padding: 5px;
    }
    .floating-nav::-webkit-scrollbar { display: none; }

    .floating-nav a {
      text-decoration: none;
      color: var(--ink-soft);
      font-family: 'DM Sans', sans-serif;
      font-size: 0.7rem;
      font-weight: 500;
      text-transform: uppercase;
      letter-spacing: 1.5px;
      padding: 8px 16px;
      border-radius: 40px;
      white-space: nowrap;
      transition: all 0.25s ease;
    }
    .floating-nav a:hover {
      background: var(--gold-pale);
      color: var(--burgundy);
    }
    .floating-nav a.nav-rsvp {
      background: var(--burgundy);
      color: var(--gold-pale);
      margin-left: 4px;
    }
    .floating-nav a.nav-rsvp:hover {
      background: var(--burgundy-soft);
      color: white;
    }

    /* ─── HERO ─── */
    .hero {
      min-height: 100vh;
      min-height: 100dvh;
      display: flex;
      flex-direction: column;
      justify-content: center;
      align-items: center;
      text-align: center;
      position: relative;
      overflow: hidden;
      padding: 100px 24px 60px;
    }
    .hero-bg {
      position: absolute; inset: 0;
      background: url('https://images.unsplash.com/photo-1519225421980-715cb0215aed?q=80&w=2070') center/cover no-repeat;
      /* Replace above URL with your actual couple photo */
      transform: scale(1.05);
      transition: transform 8s ease;
    }
    .hero-bg.loaded { transform: scale(1); }
    .hero-overlay {
      position: absolute; inset: 0;
      background:
        radial-gradient(ellipse at center, rgba(30,14,20,0.35) 0%, rgba(30,14,20,0.72) 100%);
    }
    .hero-content { position: relative; z-index: 2; max-width: 700px; }

    .hero-eyebrow {
      font-family: 'DM Sans', sans-serif;
      font-size: 0.7rem;
      font-weight: 500;
      letter-spacing: 4px;
      text-transform: uppercase;
      color: var(--gold-light);
      margin-bottom: 20px;
      opacity: 0;
      animation: fadeUp 1s ease 0.3s forwards;
    }
    .hero h1 {
      font-family: 'Cormorant Garamond', serif;
      font-size: clamp(3.2rem, 13vw, 6.5rem);
      font-weight: 300;
      line-height: 1.0;
      color: white;
      margin-bottom: 16px;
      opacity: 0;
      animation: fadeUp 1s ease 0.55s forwards;
      text-shadow: 0 2px 40px rgba(0,0,0,0.3);
    }
    .hero h1 em {
      font-style: italic;
      color: var(--gold-light);
    }
    .hero-date {
      font-family: 'DM Sans', sans-serif;
      font-size: 0.8rem;
      font-weight: 400;
      letter-spacing: 3px;
      text-transform: uppercase;
      color: rgba(255,255,255,0.75);
      margin-bottom: 36px;
      opacity: 0;
      animation: fadeUp 1s ease 0.75s forwards;
    }
    .hero-cta {
      display: inline-block;
      text-decoration: none;
      font-family: 'DM Sans', sans-serif;
      font-size: 0.72rem;
      font-weight: 500;
      letter-spacing: 3px;
      text-transform: uppercase;
      color: var(--gold-light);
      border: 1px solid var(--gold-light);
      padding: 14px 36px;
      border-radius: 40px;
      transition: all 0.3s ease;
      margin-bottom: 52px;
      opacity: 0;
      animation: fadeUp 1s ease 0.95s forwards;
    }
    .hero-cta:hover {
      background: var(--gold-light);
      color: var(--ink);
    }

    /* ─── COUNTDOWN ─── */
    #countdown {
      display: flex;
      gap: 12px;
      justify-content: center;
      opacity: 0;
      animation: fadeUp 1s ease 1.1s forwards;
    }
    .count-item {
      background: rgba(255,255,255,0.08);
      border: 1px solid rgba(255,255,255,0.15);
      backdrop-filter: blur(8px);
      padding: 14px 16px;
      border-radius: 12px;
      min-width: 68px;
      text-align: center;
    }
    .count-number {
      display: block;
      font-family: 'Cormorant Garamond', serif;
      font-size: 2rem;
      font-weight: 300;
      color: white;
      line-height: 1;
      font-variant-numeric: tabular-nums;
    }
    .count-label {
      display: block;
      font-size: 0.58rem;
      letter-spacing: 2px;
      text-transform: uppercase;
      color: var(--gold-light);
      margin-top: 5px;
    }

    @keyframes fadeUp {
      to { opacity: 1; transform: translateY(0); }
      from { opacity: 0; transform: translateY(18px); }
    }

    /* ─── SECTION COMMON ─── */
    section { position: relative; }

    .section-inner {
      max-width: 1100px;
      margin: 0 auto;
      padding: 90px 28px;
    }

    .section-label {
      font-family: 'DM Sans', sans-serif;
      font-size: 0.65rem;
      font-weight: 500;
      letter-spacing: 4px;
      text-transform: uppercase;
      color: var(--gold);
      margin-bottom: 12px;
      text-align: center;
    }
    .section-title {
      font-size: clamp(2rem, 5vw, 3rem);
      font-weight: 300;
      color: var(--burgundy);
      text-align: center;
      line-height: 1.15;
      margin-bottom: 10px;
    }
    .section-title em { font-style: italic; }
    .section-sub {
      font-size: 0.95rem;
      color: var(--muted);
      text-align: center;
      max-width: 520px;
      margin: 0 auto 52px;
      line-height: 1.7;
      font-weight: 300;
    }

    /* ─── CELEBRATION SECTION ─── */
    .celebration-section { background: var(--ivory); overflow: hidden; }

    /* Background slideshow */
    .celeb-bg-container {
      position: absolute; top: 0; left: 50%;
      width: 100vw; height: 100%;
      transform: translateX(-50%);
      z-index: 0;
      overflow: hidden;
      pointer-events: none;
    }
    .celeb-slider-track {
      display: flex; height: 100%; width: 100%;
      transition: transform 0.8s cubic-bezier(0.4,0,0.2,1);
    }
    .celeb-slide {
      flex: 0 0 100vw; height: 100%;
      background-size: cover; background-position: center;
    }
    .celeb-slider-overlay {
      position: absolute; inset: 0;
      background: rgba(250,247,242,0.82);
      z-index: 1;
    }
    .celebration-section .section-inner { position: relative; z-index: 2; }

    /* Info cards */
    .info-grid {
      align-items: stretch;
      display: grid;
      grid-template-columns: 1fr;
      gap: 20px;
    }
    @media (min-width: 768px) {
      .info-grid { grid-template-columns: repeat(3, 1fr); gap: 24px; }
    }

    .info-card {
      background: white;
      border: 1px solid var(--border);
      border-radius: 16px;
      padding: 36px 28px;
      transition: all 0.3s ease;
      box-shadow: 0 4px 24px rgba(107,15,43,0.06);
      overflow: hidden;
      min-width: 0;
      display: flex;
      flex-direction: column;
    }
    .info-card:hover {
      border-color: var(--border-strong);
      transform: translateY(-4px);
      box-shadow: 0 12px 40px rgba(107,15,43,0.1);
    }
    .info-card-icon {
      width: 38px; height: 38px;
      background: var(--gold-pale);
      border-radius: 50%;
      display: flex; align-items: center; justify-content: center;
      margin-bottom: 18px;
    }
    .info-card-icon svg { width: 16px; height: 16px; stroke: var(--gold); fill: none; stroke-width: 1.5; }
    .info-card h3 {
      font-size: 1.3rem;
      color: var(--burgundy);
      margin-bottom: 18px;
      padding-bottom: 14px;
      border-bottom: 1px solid var(--border);
      font-weight: 400;
    }
    .info-card p { font-size: 0.9rem; color: var(--ink-soft); line-height: 1.7; margin-bottom: 10px; }
    .info-card b { font-weight: 500; color: var(--ink); }
    .loc-label {
      font-size: 0.65rem;
      font-weight: 500;
      letter-spacing: 2.5px;
      text-transform: uppercase;
      color: var(--gold);
      margin-bottom: 4px;
    }
    .maps-link {
      display: inline-flex;
      align-items: center;
      gap: 5px;
      font-size: 0.8rem;
      font-weight: 500;
      color: var(--burgundy);
      text-decoration: none;
      border-bottom: 1px solid var(--border-strong);
      padding-bottom: 1px;
      transition: color 0.2s;
    }
    .maps-link:hover { color: var(--gold); }

    /* Dress code slider */
    .dress-slider {
      display: flex;
      overflow-x: auto;
      gap: 10px;
      scroll-snap-type: x mandatory;
      scrollbar-width: none;
      padding: 14px 0 24px;
      margin-bottom: 4px;
    }
    .dress-slider::-webkit-scrollbar { display: none; }
    .dress-img {
      flex: 0 0 100px;
      height: 150px;
      background-size: cover;
      background-position: center;
      border-radius: 10px;
      scroll-snap-align: start;
      box-shadow: 0 4px 12px rgba(0,0,0,0.1);
    }

    /* ─── ENTOURAGE ─── */
    .entourage-section { background: white; }

    .entourage-container { max-width: 840px; margin: 0 auto; }

    .entourage-block {
      text-align: center;
      padding: 28px 20px;
      border: 1px solid var(--border);
      border-radius: 14px;
      background: var(--ivory);
      transition: border-color 0.3s;
    }
    .entourage-block:hover { border-color: var(--border-strong); }

    .entourage-block h3, .entourage-group h3 {
      font-family: 'DM Sans', sans-serif;
      font-size: 0.62rem;
      font-weight: 500;
      letter-spacing: 3px;
      text-transform: uppercase;
      color: var(--gold);
      margin-bottom: 10px;
    }
    .entourage-block p, .entourage-group p {
      font-family: 'Cormorant Garamond', serif;
      font-size: 1.25rem;
      font-weight: 400;
      color: var(--ink);
      line-height: 1.65;
      margin: 0;
    }

    .entourage-group {
      text-align: center;
      padding: 10px 0;
    }

    .couple-block {
      background: var(--ivory);
      border: 1px solid var(--border-strong);
      border-radius: 16px;
      padding: 40px 28px;
      text-align: center;
      margin-bottom: 8px;
    }
    .couple-block p {
      font-family: 'Cormorant Garamond', serif;
      font-size: 1.9rem;
      font-weight: 300;
      font-style: italic;
      color: var(--burgundy);
      line-height: 1.5;
    }
    .couple-amp {
      font-size: 2.4rem;
      color: var(--gold);
      display: block;
      margin: 4px 0;
    }

    .egrid-2 { display: grid; grid-template-columns: 1fr; gap: 16px; }
    .egrid-3 { display: grid; grid-template-columns: 1fr; gap: 16px; }
    @media (min-width: 640px) {
      .egrid-2 { grid-template-columns: 1fr 1fr; gap: 20px; }
      .egrid-3 { grid-template-columns: 1fr 1fr 1fr; gap: 16px; }
    }

    .sponsors-wrap {
      background: var(--ivory);
      border: 1px solid var(--border);
      border-radius: 16px;
      padding: 36px 28px;
    }
    .sponsors-cols {
      display: flex;
      flex-direction: column;
      gap: 28px;
      margin-top: 8px;
    }
    @media (min-width: 640px) {
      .sponsors-cols { flex-direction: row; justify-content: space-around; }
    }
    .sponsors-col h4 {
      font-size: 1.1rem;
      color: var(--burgundy);
      margin-bottom: 14px;
      padding-bottom: 8px;
      border-bottom: 1px solid var(--border-strong);
      font-weight: 400;
      font-family: 'Cormorant Garamond', serif;
    }
    .sponsors-col p {
      font-family: 'Cormorant Garamond', serif;
      font-size: 1.15rem;
      color: var(--ink);
      margin: 7px 0;
      line-height: 1.4;
    }

    .secondary-role {
      font-family: 'DM Sans', sans-serif;
      font-size: 0.6rem;
      font-weight: 500;
      letter-spacing: 2.5px;
      text-transform: uppercase;
      color: var(--gold);
      margin-top: 22px;
      margin-bottom: 4px;
    }

    .e-stack { display: flex; flex-direction: column; gap: 16px; }

    /* ─── REGISTRY ─── */
    .registry-section { background: var(--ivory-dark); }

    .registry-grid {
      display: grid;
      grid-template-columns: repeat(auto-fill, minmax(150px, 1fr));
      gap: 12px;
      max-width: 820px;
      margin: 0 auto 36px;
    }
    .gift-item {
      background: white;
      border: 1px solid var(--border);
      border-radius: 12px;
      padding: 22px 14px;
      font-family: 'DM Sans', sans-serif;
      font-size: 0.82rem;
      font-weight: 500;
      color: var(--burgundy);
      text-align: center;
      transition: all 0.25s;
      cursor: default;
    }
    .gift-item:hover { border-color: var(--border-strong); transform: translateY(-2px); box-shadow: 0 6px 20px rgba(107,15,43,0.08); }
    .gift-item.taken {
      text-decoration: line-through;
      opacity: 0.45;
      color: var(--muted);
      background: #f4f4f4;
      cursor: default;
    }
    .gift-item.taken:hover { transform: none; box-shadow: none; }

    .registry-loading {
      grid-column: 1 / -1;
      text-align: center;
      color: var(--muted);
      font-style: italic;
      padding: 36px 0;
      font-size: 0.9rem;
    }
    .registry-error {
      grid-column: 1 / -1;
      text-align: center;
      color: #c0392b;
      font-size: 0.85rem;
      padding: 24px;
      background: #fff5f5;
      border-radius: 12px;
    }

    /* ─── RSVP ─── */
    .rsvp-section {
      background: var(--burgundy);
      position: relative;
      overflow: hidden;
    }
    .rsvp-section::before {
      content: '';
      position: absolute;
      top: -120px; right: -120px;
      width: 500px; height: 500px;
      background: radial-gradient(circle, rgba(184,145,63,0.12) 0%, transparent 70%);
      pointer-events: none;
    }
    .rsvp-section::after {
      content: '';
      position: absolute;
      bottom: -80px; left: -80px;
      width: 400px; height: 400px;
      background: radial-gradient(circle, rgba(184,145,63,0.08) 0%, transparent 70%);
      pointer-events: none;
    }
    .rsvp-section .section-inner { position: relative; z-index: 1; max-width: 580px; }
    .rsvp-section .section-label { color: var(--gold-light); }
    .rsvp-section .section-title { color: white; }
    .rsvp-section .section-sub { color: rgba(255,255,255,0.65); }
    .rsvp-deadline {
      display: inline-block;
      background: rgba(184,145,63,0.15);
      border: 1px solid rgba(184,145,63,0.35);
      border-radius: 40px;
      padding: 6px 18px;
      font-size: 0.78rem;
      color: var(--gold-light);
      letter-spacing: 1px;
      margin-bottom: 32px;
    }

    .rsvp-form { display: flex; flex-direction: column; gap: 16px; }
    .form-row { display: flex; gap: 12px; }
    @media (max-width: 480px) { .form-row { flex-direction: column; } }

    .field-wrap { display: flex; flex-direction: column; gap: 6px; width: 100%; }
    .field-label {
      font-size: 0.65rem;
      font-weight: 500;
      letter-spacing: 2px;
      text-transform: uppercase;
      color: var(--gold-light);
    }
    input, select, textarea {
      width: 100%;
      padding: 14px 16px;
      font-size: 0.95rem;
      font-family: 'DM Sans', sans-serif;
      background: rgba(255,255,255,0.1);
      border: 1px solid rgba(255,255,255,0.18);
      border-radius: 10px;
      color: white;
      outline: none;
      transition: border-color 0.25s, background 0.25s;
      box-sizing: border-box;
    }
    input::placeholder, textarea::placeholder { color: rgba(255,255,255,0.35); }
    input:focus, select:focus, textarea:focus {
      border-color: var(--gold-light);
      background: rgba(255,255,255,0.15);
    }
    select { cursor: pointer; }
    select option { background: var(--burgundy); color: white; }
    textarea { resize: vertical; min-height: 100px; }

    .char-counter {
      text-align: right;
      font-size: 0.72rem;
      color: rgba(255,255,255,0.4);
      margin-top: -10px;
    }

    .btn-submit {
      background: var(--gold);
      color: white;
      border: none;
      border-radius: 10px;
      padding: 16px;
      font-family: 'DM Sans', sans-serif;
      font-size: 0.78rem;
      font-weight: 500;
      letter-spacing: 3px;
      text-transform: uppercase;
      cursor: pointer;
      transition: all 0.3s ease;
      width: 100%;
    }
    .btn-submit:hover { background: var(--gold-light); transform: translateY(-1px); box-shadow: 0 6px 20px rgba(0,0,0,0.2); }
    .btn-submit:disabled { opacity: 0.55; cursor: not-allowed; transform: none; }

    .rsvp-success {
      display: none;
      background: rgba(255,255,255,0.08);
      border: 1px solid rgba(184,145,63,0.35);
      border-radius: 16px;
      padding: 52px 32px;
      text-align: center;
      margin-top: 32px;
    }
    .rsvp-success-icon {
      width: 32px; height: 32px;
      background: var(--gold);
      transform: rotate(45deg);
      margin: 0 auto 20px;
      opacity: 0.85;
    }
    .rsvp-success h3 { font-size: 2rem; color: var(--gold-light); margin-bottom: 10px; font-weight: 300; }
    .rsvp-success p { font-size: 0.95rem; color: rgba(255,255,255,0.75); line-height: 1.7; }

    /* ─── MESSAGES ─── */
    .messages-section { background: white; }
    .attendance-counter {
      font-family: 'Cormorant Garamond', serif;
      font-size: 1.15rem;
      font-style: italic;
      color: var(--gold);
      text-align: center;
      margin-top: -28px;
      margin-bottom: 40px;
    }
    #message-list {
      display: flex;
      flex-direction: column;
      gap: 12px;
      align-items: center;
    }
    .message-card {
      background: var(--ivory);
      border: 1px solid var(--border);
      border-left: 3px solid var(--gold);
      border-radius: 0 12px 12px 0;
      padding: 18px 22px;
      width: 100%;
      max-width: 520px;
      box-shadow: 0 2px 12px rgba(107,15,43,0.05);
    }
    .message-card p {
      font-family: 'Cormorant Garamond', serif;
      font-size: 1.1rem;
      font-style: italic;
      color: var(--ink);
      line-height: 1.6;
      margin-bottom: 8px;
    }
    .message-card span {
      font-size: 0.78rem;
      font-weight: 500;
      color: var(--muted);
      letter-spacing: 0.5px;
    }

    /* ─── FAQ ─── */
    .faq-section { background: var(--ivory-dark); }
    .faq-container { max-width: 740px; margin: 0 auto; }

    .faq-item {
      background: white;
      border: 1px solid var(--border);
      border-radius: 12px;
      margin-bottom: 10px;
      overflow: hidden;
      transition: border-color 0.3s;
    }
    .faq-item:hover { border-color: var(--border-strong); }

    .faq-question {
      width: 100%;
      text-align: left;
      padding: 18px 22px;
      background: none;
      border: none;
      font-family: 'DM Sans', sans-serif;
      font-size: 0.9rem;
      font-weight: 500;
      color: var(--ink);
      cursor: pointer;
      display: flex;
      justify-content: space-between;
      align-items: center;
      gap: 16px;
      transition: color 0.25s;
    }
    .faq-question:hover { color: var(--burgundy); }
    .faq-plus {
      flex-shrink: 0;
      width: 22px; height: 22px;
      border-radius: 50%;
      background: var(--gold-pale);
      color: var(--gold);
      font-size: 1.1rem;
      font-weight: 300;
      display: flex; align-items: center; justify-content: center;
      line-height: 1;
      transition: transform 0.3s, background 0.25s;
    }
    .faq-question.active .faq-plus { transform: rotate(45deg); background: var(--gold-pale); }
    .faq-answer { max-height: 0; overflow: hidden; transition: max-height 0.35s ease; }
    .faq-answer-content {
      padding: 0 22px 20px;
      font-size: 0.88rem;
      color: var(--ink-soft);
      line-height: 1.75;
    }

    /* ─── FOOTER ─── */
    footer {
      background: var(--ink);
      padding: 72px 28px 52px;
      text-align: center;
    }
    .footer-hashtag {
      font-family: 'Cormorant Garamond', serif;
      font-size: clamp(1.4rem, 5vw, 2.4rem);
      font-weight: 300;
      font-style: italic;
      color: var(--gold);
      margin-bottom: 10px;
    }
    .footer-names {
      font-size: 0.8rem;
      font-weight: 400;
      letter-spacing: 3px;
      text-transform: uppercase;
      color: rgba(184,145,63,0.6);
      margin-bottom: 28px;
    }
    .footer-divider {
      width: 60px; height: 1px;
      background: var(--border-strong);
      margin: 0 auto 22px;
    }
    .footer-contact {
      font-size: 0.8rem;
      color: rgba(255,255,255,0.4);
    }
    .footer-contact a { color: var(--gold); text-underline-offset: 3px; }

    /* ─── MOBILE ─── */
    @media (max-width: 480px) {
      .floating-nav {
        top: 12px;
        width: calc(100vw - 24px);
        padding: 4px 8px;
        gap: 2px;
      }
      .floating-nav a { font-size: 0.6rem; padding: 7px 10px; letter-spacing: 1px; }
    }
  </style>
</head>
<body>

  <!-- NAV -->
  <nav class="floating-nav">
    <a href="#home">Save The Date</a>
    <a href="#celebration">Celebration</a>
    <a href="#entourage">Entourage</a>
    <a href="#registry">Registry</a>
    <a href="#messages">Messages</a>
    <a href="#faqs">FAQ</a>
    <a href="#rsvp" class="nav-rsvp">RSVP</a>
  </nav>

  <!-- HERO -->
  <section class="hero" id="home">
    <div class="hero-bg" id="heroBg"></div>
    <div class="hero-overlay"></div>
    <div class="hero-content">
      <p class="hero-eyebrow">Save the Date</p>
      <h1>Edward &amp; <em>Clara</em></h1>
      <p class="hero-date">June 20, 2026 &nbsp;·&nbsp; Caloocan City</p>
      <a href="#celebration" class="hero-cta">View Details</a>
      <div id="countdown">
        <div class="count-item"><span class="count-number" id="days">00</span><span class="count-label">Days</span></div>
        <div class="count-item"><span class="count-number" id="hours">00</span><span class="count-label">Hours</span></div>
        <div class="count-item"><span class="count-number" id="minutes">00</span><span class="count-label">Mins</span></div>
        <div class="count-item"><span class="count-number" id="seconds">00</span><span class="count-label">Secs</span></div>
      </div>
    </div>
  </section>

  <!-- CELEBRATION -->
  <section class="celebration-section" id="celebration">
    <div class="celeb-bg-container">
      <div class="celeb-slider-track" id="celebSliderTrack">
        <div class="celeb-slide" style="background-image:url('https://images.unsplash.com/photo-1532712938310-34cb3982ef74?q=80&w=1170&auto=format&fit=crop');"></div>
        <div class="celeb-slide" style="background-image:url('https://images.unsplash.com/photo-1520854221256-17451cc331bf?q=80&w=1170&auto=format&fit=crop');"></div>
        <div class="celeb-slide" style="background-image:url('https://images.unsplash.com/photo-1606800052052-a08af7148866?q=80&w=1170&auto=format&fit=crop');"></div>
        <div class="celeb-slide" style="background-image:url('https://images.unsplash.com/photo-1519741497674-611481863552?q=80&w=1170&auto=format&fit=crop');"></div>
      </div>
      <div class="celeb-slider-overlay"></div>
    </div>
    <div class="section-inner">
      <div class="reveal">
        <p class="section-label">The Details</p>
        <div class="ornament"><div class="ornament-diamond"></div></div>
        <h2 class="section-title">The <em>Celebration</em></h2>
        <p class="section-sub">We can't wait to share our special day with you. Here are all the details you need.</p>
      </div>
      <div class="info-grid reveal">

        <!-- WHEN -->
        <div class="info-card">
          <div class="info-card-icon">
            <svg viewBox="0 0 24 24"><rect x="3" y="4" width="18" height="18" rx="3"/><line x1="16" y1="2" x2="16" y2="6"/><line x1="8" y1="2" x2="8" y2="6"/><line x1="3" y1="10" x2="21" y2="10"/></svg>
          </div>
          <h3>When</h3>
          <p><b>Ceremony</b><br>June 20, 2026 · 9:00 AM</p>
          <p style="margin-top:14px;"><b>Reception</b><br>June 20, 2026 · 11:00 AM</p>
        </div>

        <!-- WHERE -->
        <div class="info-card">
          <div class="info-card-icon">
            <svg viewBox="0 0 24 24"><path d="M12 2C8.13 2 5 5.13 5 9c0 5.25 7 13 7 13s7-7.75 7-13c0-3.87-3.13-7-7-7z"/><circle cx="12" cy="9" r="2.5"/></svg>
          </div>
          <h3>Where</h3>
          <div style="margin-bottom:18px;">
            <p class="loc-label">Ceremony</p>
            <p>Shrine of Our Lady of Grace</p>
            <a href="https://maps.app.goo.gl/5CCcBkTjDdrai8D79" target="_blank" class="maps-link">Open in Maps →</a>
          </div>
          <div>
            <p class="loc-label">Reception</p>
            <p>Chef Patrick's Kitchen</p>
            <a href="https://maps.app.goo.gl/ffemshFVf4E9JXGy7" target="_blank" class="maps-link">Open in Maps →</a>
          </div>
        </div>

        <!-- DRESS CODE -->
        <div class="info-card">
          <div class="info-card-icon">
            <svg viewBox="0 0 24 24"><path d="M12 3L8 7H5l-2 5h18l-2-5h-3L12 3z"/><rect x="3" y="12" width="18" height="9" rx="2"/></svg>
          </div>
          <h3>Dress Code</h3>
          <div style="flex:1;">
          <p><b>Semi-Formal</b><br>Burgundy Wine &amp; Gold</p>
          <p style="font-size:0.78rem; color:var(--muted); margin-top:6px;">Suit or Barong for men · Floor-length or Cocktail for women</p>
          <p style="font-size:0.75rem; color:var(--gold); margin-top:12px; letter-spacing:0.5px;">Swipe for inspiration →</p>
          </div>
          <div class="dress-slider">
            <div class="dress-img" style="background-image:url('https://images.unsplash.com/photo-1767884044863-921f06ccf54e?q=80&w=300');"></div>
            <div class="dress-img" style="background-image:url('https://i.pinimg.com/736x/e9/43/71/e94371b3139ce72895a8b78e19c878ef.jpg');"></div>
            <div class="dress-img" style="background-image:url('https://images.unsplash.com/photo-1746366618815-beaf21b2b109?q=80&w=300');"></div>
            <div class="dress-img" style="background-image:url('https://images.unsplash.com/photo-1716946861089-199f9635351d?q=80&w=300');"></div>
            <div class="dress-img" style="background-image:url('https://i.pinimg.com/736x/c6/cc/40/c6cc40064cb4d3b12bb498c1c3fb2955.jpg');"></div>
            <div class="dress-img" style="background-image:url('https://content.woolovers.com/img/747x856/fbcec-219181_j86m_deepmaroon_m_8-v2.jpg');"></div>
            <div class="dress-img" style="background-image:url('https://presleycouture.com/cdn/shop/products/LLP_9554_1200x1200.jpg?v=1691787191');"></div>
          </div>
        </div>

      </div>
    </div>
  </section>

  <!-- ENTOURAGE -->
  <section class="entourage-section" id="entourage">
    <div class="section-inner">
      <div class="reveal">
        <p class="section-label">The People</p>
        <div class="ornament"><div class="ornament-diamond"></div></div>
        <h2 class="section-title">The <em>Entourage</em></h2>
        <p class="section-sub">The people who will stand with us on our most important day.</p>
      </div>

      <div class="entourage-container e-stack reveal">

        <!-- Couple -->
        <div class="couple-block">
          <p>Clara Mai M. Alvarez<span class="couple-amp">&amp;</span>Edward DM. Cabalquinto</p>
        </div>

        <!-- Parents -->
        <div class="egrid-2">
          <div class="entourage-block">
            <h3>Parents of the Bride</h3>
            <p>Francisco Alvarez<br>Laura Alvarez</p>
          </div>
          <div class="entourage-block">
            <h3>Parents of the Groom</h3>
            <p>Rose Lynette Cabalquinto</p>
            <h3 style="margin-top:14px;">Grandfather of the Groom</h3>
            <p>Cosmero Del Mundo</p>
          </div>
        </div>

        <!-- Best Man / Maid of Honor -->
        <div class="egrid-2">
          <div class="entourage-block">
            <h3>Best Man</h3>
            <p>James Ofiangga</p>
          </div>
          <div class="entourage-block">
            <h3>Maid of Honor</h3>
            <p>Abigail Goyal</p>
          </div>
        </div>

        <!-- Groomsmen / Bridesmaids -->
        <div class="egrid-2">
          <div class="entourage-block">
            <h3>Groomsmen</h3>
            <p>Marcus Kobe Arriola<br>Elijah Magno</p>
          </div>
          <div class="entourage-block">
            <h3>Bridesmaids</h3>
            <p>Bea Mae Glino<br>Kimberly Santos Flores</p>
          </div>
        </div>

        <!-- Principal Sponsors -->
        <div class="sponsors-wrap">
          <div class="entourage-group">
            <h3>Principal Sponsors</h3>
          </div>
          <div class="sponsors-cols">
            <div class="sponsors-col">
              <h4>Ninongs</h4>
              <p>Ronald Reyes</p><p>Arnold Marcos</p><p>Nilo Burnea</p>
              <p>Ronnel Abrera</p><p>Dexter Solon</p><p>Rex Aldo Manzanillo</p>
            </div>
            <div class="sponsors-col">
              <h4>Ninangs</h4>
              <p>Flerida Batang</p><p>Margie Marcos</p><p>Lily Burnea</p>
              <p>Candy Liaban</p><p>Daisy Arriola</p><p>Lovely Manzanillo</p>
            </div>
          </div>
        </div>

        <!-- Secondary Sponsors -->
        <div class="entourage-block">
          <h3>Secondary Sponsors</h3>
          <p class="secondary-role">Candle Sponsors</p>
          <p>Eduardo Balmaceda &amp; Eloisa Reign Mellendrez</p>
          <p class="secondary-role">Veil Sponsors</p>
          <p>Santie Gatchalian &amp; Lei Xjiemiz Del Mundo Cabalquinto</p>
          <p class="secondary-role">Cord Sponsors</p>
          <p>Rainier Joseph Sanchez &amp; Xandra Jane Dating</p>
        </div>

        <!-- Bearers -->
        <div class="egrid-3">
          <div class="entourage-block">
            <h3>Ring Bearer</h3>
            <p>Jericho Alvarez</p>
          </div>
          <div class="entourage-block">
            <h3>Coin Bearer</h3>
            <p>Karl Matthew Crespo</p>
          </div>
          <div class="entourage-block">
            <h3>Bible Bearer</h3>
            <p>Joeseph Batang</p>
          </div>
        </div>

        <!-- Flower Girls -->
        <div class="entourage-block">
          <h3>Flower Girls</h3>
          <p>Krystal Maiden Crespo · Isaiah Laurelle Mellendrez · Kissjane Orola</p>
        </div>

      </div>
    </div>
  </section>

  <!-- REGISTRY -->
  <section class="registry-section" id="registry">
    <div class="section-inner">
      <div class="reveal">
        <p class="section-label">Gift Registry</p>
        <div class="ornament"><div class="ornament-diamond"></div></div>
        <h2 class="section-title">Wedding <em>Registry</em></h2>
        <p class="section-sub">Your presence is the greatest gift. But if you wish to honor us further, here's our wishlist.</p>
      </div>
      <div class="registry-grid reveal" id="registryDisplay">
        <div class="registry-loading">Loading registry...</div>
      </div>
      <p style="text-align:center; font-size:0.85rem; color:var(--muted); font-style:italic;">Scroll down to RSVP ↓</p>
    </div>
  </section>

  <!-- RSVP -->
  <section class="rsvp-section" id="rsvp">
    <div class="section-inner">
      <div class="reveal">
        <p class="section-label">Your Response</p>
        <div class="ornament" style="--gold:rgba(184,145,63,0.5);"><div class="ornament-diamond" style="background:var(--gold-light);"></div></div>
        <h2 class="section-title">Will You <em>Attend?</em></h2>
        <p class="section-sub">We'd love to know if you can join us on our special day.</p>
        <p class="rsvp-deadline">Please respond by May 20, 2026</p>
      </div>

      <form id="rsvpForm" class="rsvp-form reveal">
        <div class="form-row">
          <div class="field-wrap">
            <label class="field-label">First Name</label>
            <input type="text" id="firstName" placeholder="e.g. Maria" required>
          </div>
          <div class="field-wrap">
            <label class="field-label">Last Name</label>
            <input type="text" id="lastName" placeholder="e.g. Santos" required>
          </div>
        </div>
        <div class="field-wrap">
          <label class="field-label">Will you be attending?</label>
          <select id="attendance" required>
            <option value="be attending">Joyfully Attend</option>
            <option value="not be attending">Regretfully Decline</option>
          </select>
        </div>
        <div class="field-wrap">
          <label class="field-label">Wedding Gift (Optional)</label>
          <select id="giftSelection">
            <option value="No gift selected">— Select a gift from our registry —</option>
          </select>
        </div>
        <div class="field-wrap">
          <label class="field-label">Message for the Couple</label>
          <textarea id="guestMessage" rows="4" placeholder="A short, sweet message for Edward &amp; Clara..." maxlength="300" required></textarea>
          <div class="char-counter"><span id="charCount">0</span> / 300</div>
        </div>
        <button type="submit" class="btn-submit">Send RSVP</button>
      </form>

      <div class="rsvp-success" id="rsvpSuccess">
        <div class="rsvp-success-icon"></div>
        <h3>Thank you!</h3>
        <p id="rsvpSuccessMsg">We've received your RSVP!</p>
      </div>
    </div>
  </section>

  <!-- MESSAGES -->
  <section class="messages-section" id="messages">
    <div class="section-inner">
      <div class="reveal">
        <p class="section-label">From Our Guests</p>
        <div class="ornament"><div class="ornament-diamond"></div></div>
        <h2 class="section-title">Messages to the <em>Couple</em></h2>
      </div>
      <p class="attendance-counter" id="attendanceCounter">Loading...</p>
      <div id="message-list"></div>
    </div>
  </section>

  <!-- FAQ -->
  <section class="faq-section" id="faqs">
    <div class="section-inner">
      <div class="reveal">
        <p class="section-label">Questions</p>
        <div class="ornament"><div class="ornament-diamond"></div></div>
        <h2 class="section-title">Frequently <em>Asked</em></h2>
        <p class="section-sub">Everything you need to know about the big day.</p>
      </div>
      <div class="faq-container reveal">

        <div class="faq-item">
          <button class="faq-question">What time should I arrive? <span class="faq-plus">+</span></button>
          <div class="faq-answer"><div class="faq-answer-content">The ceremony begins promptly at 9:00 AM. We recommend arriving 20–30 minutes early to find your seat and enjoy the pre-ceremony music.</div></div>
        </div>

        <div class="faq-item">
          <button class="faq-question">Is there parking available? <span class="faq-plus">+</span></button>
          <div class="faq-answer"><div class="faq-answer-content">Yes, free parking is available on-site at both the ceremony and reception venues.</div></div>
        </div>

        <div class="faq-item">
          <button class="faq-question">When is the RSVP deadline? <span class="faq-plus">+</span></button>
          <div class="faq-answer"><div class="faq-answer-content">Please RSVP by May 20, 2026 via the form on this website.</div></div>
        </div>

        <div class="faq-item">
          <button class="faq-question">What is the dress code? <span class="faq-plus">+</span></button>
          <div class="faq-answer"><div class="faq-answer-content">The dress code is <strong>Semi-Formal</strong> in our theme colors of <strong>Burgundy Wine &amp; Gold</strong>. We suggest a Suit or Barong for men and a Floor-length or Cocktail dress for women.</div></div>
        </div>

        <div class="faq-item">
          <button class="faq-question">Are there colors I should avoid? <span class="faq-plus">+</span></button>
          <div class="faq-answer"><div class="faq-answer-content">We kindly ask that you avoid shades of ivory or white, as those are reserved for the bride. Feel free to wear any shade of Burgundy or Gold to complement our theme!</div></div>
        </div>

        <div class="faq-item">
          <button class="faq-question">Can I bring a plus one? <span class="faq-plus">+</span></button>
          <div class="faq-answer"><div class="faq-answer-content">Due to limited seating, we can only accommodate guests specifically listed on your invitation. Thank you for understanding.</div></div>
        </div>

        <div class="faq-item">
          <button class="faq-question">Are children invited? <span class="faq-plus">+</span></button>
          <div class="faq-answer"><div class="faq-answer-content">We love your little ones, but due to venue limitations we can only accommodate children who are part of the entourage. We hope you understand and enjoy a night to yourselves!</div></div>
        </div>

        <div class="faq-item">
          <button class="faq-question">What if I arrive late to the ceremony? <span class="faq-plus">+</span></button>
          <div class="faq-answer"><div class="faq-answer-content">If you arrive after the ceremony has begun, please do not walk down the aisle. Kindly ask our coordinator for assistance or wait to join us at the reception. Thank you for your understanding.</div></div>
        </div>

        <div class="faq-item">
          <button class="faq-question">Can I take photos or videos? <span class="faq-plus">+</span></button>
          <div class="faq-answer"><div class="faq-answer-content">
            We'd love for you to help capture our joy! We do ask that you:<br><br>
            <strong>Be mindful of the pros</strong> — please don't obstruct our photography and videography team.<br><br>
            <strong>Stay clear of the aisle</strong> — to keep the ceremony flowing beautifully, please stay seated during the processional. We can't wait to see what you capture from your seat!
          </div></div>
        </div>

      </div>
    </div>
  </section>

  <!-- FOOTER -->
  <footer>
    <p class="footer-hashtag">#MaiOneAndOnlyEdward</p>
    <p class="footer-names">With Love, Edward &amp; Clara</p>
    <div class="footer-divider"></div>
    <p class="footer-contact">
      Have questions? Reach us on
      <a href="https://m.me/" target="_blank">Facebook Messenger</a>
      <!-- Replace href with your actual contact link -->
    </p>
  </footer>

<script>
  const SCRIPT_URL = 'https://script.google.com/macros/s/AKfycbwatQQWXjV74tz0UH8-TR59840ybdiawnkSFCzcztljqBOZVq-s2Q8x-UC8qH9fobJF/exec';
  const TOTAL_GUESTS = 124;

  const giftRegistry = [
    { id: 1, name: "Microwave Oven", repeatable: false },
    { id: 2, name: "Robot Vacuum", repeatable: false },
    { id: 3, name: "Coffee/Espresso Machine", repeatable: false },
    { id: 4, name: "Air Purifier", repeatable: false },
    { id: 5, name: "Blender", repeatable: false },
    { id: 6, name: "Cooking Ware Set", repeatable: true },
    { id: 7, name: "Cash Gift", repeatable: true },
    { id: 8, name: "SURPRISE US!", repeatable: true }
  ];

  let takenGifts = [], savedMessages = [];

  async function loadInitialData() {
    try {
      const res = await fetch(SCRIPT_URL);
      if (!res.ok) throw new Error('Network error');
      const data = await res.json();
      takenGifts = data.takenGifts || [];
      savedMessages = data.savedMessages || [];
      initRegistry();
      displayMessages();
    } catch (err) {
      console.error(err);
      document.getElementById('registryDisplay').innerHTML = '<div class="registry-error">Registry temporarily unavailable. Please check back later.</div>';
      initRegistrySelect();
      displayMessages();
    }
  }

  function initRegistrySelect() {
    const sel = document.getElementById('giftSelection');
    sel.innerHTML = '<option value="No gift selected">— Select a gift from our registry —</option>';
    giftRegistry.forEach(g => {
      const o = document.createElement('option');
      o.value = g.name; o.dataset.id = g.id; o.innerText = g.name;
      sel.appendChild(o);
    });
  }

  function initRegistry() {
    const display = document.getElementById('registryDisplay');
    const sel = document.getElementById('giftSelection');
    display.innerHTML = '';
    sel.innerHTML = '<option value="No gift selected">— Select a gift from our registry —</option>';
    giftRegistry.forEach(g => {
      const taken = !g.repeatable && takenGifts.includes(g.id);
      const el = document.createElement('div');
      el.className = 'gift-item' + (taken ? ' taken' : '');
      el.innerText = g.name + (taken ? '\n(Taken)' : '');
      display.appendChild(el);
      const o = document.createElement('option');
      o.value = g.name; o.dataset.id = g.id;
      o.innerText = g.name + (taken ? ' (Taken)' : '');
      if (taken) o.disabled = true;
      sel.appendChild(o);
    });
  }

  function displayMessages() {
    const list = document.getElementById('message-list');
    list.innerHTML = '';
    let count = 0;
    savedMessages.forEach(d => {
      if (d.status === 'be attending') count++;
      const card = document.createElement('div');
      card.className = 'message-card';
      card.innerHTML = `<p>"${d.msg}"</p><span>— ${d.name} will ${d.status}</span>`;
      list.appendChild(card);
    });
    document.getElementById('attendanceCounter').innerText = `${count} out of ${TOTAL_GUESTS} will attend our wedding`;
  }

  // Char counter
  const ta = document.getElementById('guestMessage');
  const cc = document.getElementById('charCount');
  ta.addEventListener('input', () => cc.textContent = ta.value.length);

  // RSVP submit
  document.getElementById('rsvpForm').addEventListener('submit', async function(e) {
    e.preventDefault();
    const btn = document.querySelector('.btn-submit');
    btn.innerText = 'Sending...'; btn.disabled = true;

    const firstName = document.getElementById('firstName').value;
    const lastName  = document.getElementById('lastName').value;
    const giftName  = document.getElementById('giftSelection').value;
    const giftId    = document.getElementById('giftSelection').selectedOptions[0].dataset.id;
    const status    = document.getElementById('attendance').value;
    const msg       = document.getElementById('guestMessage').value;
    const giftObj   = giftRegistry.find(g => g.id == giftId);

    try {
      await fetch(SCRIPT_URL, {
        method: 'POST', mode: 'no-cors',
        headers: { 'Content-Type': 'text/plain;charset=utf-8' },
        body: JSON.stringify({ name: firstName + ' ' + lastName, giftName, giftId: giftId ? parseInt(giftId) : null, repeatable: giftObj ? giftObj.repeatable : true, status, msg })
      });

      const form = document.getElementById('rsvpForm');
      const box  = document.getElementById('rsvpSuccess');
      const txt  = document.getElementById('rsvpSuccessMsg');
      txt.textContent = status === 'be attending'
        ? `We've received your RSVP, ${firstName}! We can't wait to celebrate with you on June 20.`
        : `We're sorry you can't make it, ${firstName}. Thank you for letting us know — you'll be missed!`;
      form.style.display = 'none';
      box.style.display  = 'block';
      setTimeout(() => loadInitialData(), 1500);
    } catch (err) {
      alert('Error sending RSVP. Please try again.');
      btn.innerText = 'Send RSVP'; btn.disabled = false;
    }
  });

  // Celebration slideshow
  const track = document.getElementById('celebSliderTrack');
  const slides = document.querySelectorAll('.celeb-slide');
  let cur = 0;
  if (track && slides.length) {
    setInterval(() => {
      cur = (cur + 1) % slides.length;
      track.style.transform = `translateX(-${cur * 100}vw)`;
    }, 5000);
  }

  // FAQ accordion
  document.querySelectorAll('.faq-question').forEach(q => {
    q.addEventListener('click', () => {
      const active = document.querySelector('.faq-question.active');
      if (active && active !== q) { active.classList.remove('active'); active.nextElementSibling.style.maxHeight = null; }
      q.classList.toggle('active');
      const ans = q.nextElementSibling;
      ans.style.maxHeight = q.classList.contains('active') ? ans.scrollHeight + 'px' : null;
    });
  });

  // Countdown — locked to Philippine Standard Time
  const target = new Date('June 20, 2026 09:00:00 GMT+0800').getTime();
  setInterval(() => {
    const d = target - Date.now();
    if (d < 0) return;
    document.getElementById('days').innerText    = Math.floor(d / 86400000).toString().padStart(2,'0');
    document.getElementById('hours').innerText   = Math.floor((d % 86400000) / 3600000).toString().padStart(2,'0');
    document.getElementById('minutes').innerText = Math.floor((d % 3600000) / 60000).toString().padStart(2,'0');
    document.getElementById('seconds').innerText = Math.floor((d % 60000) / 1000).toString().padStart(2,'0');
  }, 1000);

  // Scroll reveal
  const observer = new IntersectionObserver((entries) => {
    entries.forEach(e => { if (e.isIntersecting) { e.target.classList.add('visible'); observer.unobserve(e.target); } });
  }, { threshold: 0.12 });
  document.querySelectorAll('.reveal').forEach(el => observer.observe(el));

  // Hero parallax bg trigger
  document.getElementById('heroBg').classList.add('loaded');

  loadInitialData();
</script>
</body>
</html>
