import { useEffect, useRef, useState } from 'react';

const STORAGE_KEY = 'plink.recent.v1';
const API_BASE_URL = (import.meta.env.VITE_API_BASE_URL || '').trim().replace(/\/+$/, '');

function readRecent() {
  try {
    const entries = JSON.parse(localStorage.getItem(STORAGE_KEY) || '[]');
    return Array.isArray(entries)
      ? entries.filter((entry) => entry?.shortUrl && entry?.originalUrl).slice(0, 5)
      : [];
  } catch {
    return [];
  }
}

function LinkMark({ small = false }) {
  return (
    <span className={`brand-mark${small ? ' brand-mark-small' : ''}`} aria-hidden="true">
      <span />
      <span />
    </span>
  );
}

function ChainIcon({ className = '' }) {
  return (
    <svg className={className} viewBox="0 0 24 24" fill="none" aria-hidden="true">
      <path d="M10.6 13.4a5 5 0 0 0 7.1 0l2-2a5 5 0 0 0-7.1-7.1l-1.1 1.1M13.4 10.6a5 5 0 0 0-7.1 0l-2 2a5 5 0 0 0 7.1 7.1l1.1-1.1" stroke="currentColor" strokeWidth="1.7" strokeLinecap="round" strokeLinejoin="round" />
    </svg>
  );
}

function MailIcon({ className = '' }) {
  return (
    <svg className={className} viewBox="0 0 24 24" fill="none" aria-hidden="true">
      <rect x="3.5" y="5" width="17" height="14" rx="3" stroke="currentColor" strokeWidth="1.7" />
      <path d="m5 7 7 5.5L19 7" stroke="currentColor" strokeWidth="1.7" strokeLinecap="round" strokeLinejoin="round" />
    </svg>
  );
}

function ShieldIcon() {
  return (
    <svg viewBox="0 0 16 16" fill="none" aria-hidden="true">
      <path d="M8 1.8 13 4v3.3c0 3.1-2.1 5.7-5 6.9-2.9-1.2-5-3.8-5-6.9V4l5-2.2Z" stroke="currentColor" strokeWidth="1.3" strokeLinejoin="round" />
      <path d="m5.8 7.8 1.5 1.5 3-3" stroke="currentColor" strokeWidth="1.3" strokeLinecap="round" strokeLinejoin="round" />
    </svg>
  );
}

function ArrowIcon({ className = '' }) {
  return (
    <svg className={className} viewBox="0 0 24 24" fill="none" aria-hidden="true">
      <path d="M5 12h14m-6-6 6 6-6 6" stroke="currentColor" strokeWidth="1.8" strokeLinecap="round" strokeLinejoin="round" />
    </svg>
  );
}

function validateUrl(value) {
  if (!value.trim()) return 'Add the URL you want to shorten.';
  try {
    const parsed = new URL(value.trim());
    if (!['http:', 'https:'].includes(parsed.protocol) || !parsed.hostname.includes('.')) {
      return 'Enter a valid link that starts with http:// or https://.';
    }
    return '';
  } catch {
    return 'Enter a valid link that starts with http:// or https://.';
  }
}

function validateEmail(value) {
  if (!value.trim()) return 'Add an email address so we can send you the link.';
  return /^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(value.trim())
    ? ''
    : 'Enter a valid email address.';
}

function errorMessage(payload, status) {
  if (payload?.message && typeof payload.message === 'object') {
    return Object.values(payload.message).join(' ');
  }
  if (typeof payload?.message === 'string' && payload.message.trim()) return payload.message;
  if (status === 429) return 'You’ve made a lot of links in a short time. Please try again in a minute.';
  return 'We couldn’t create your link. Please try again in a moment.';
}

function formatDate(value) {
  const date = new Date(value);
  if (Number.isNaN(date.getTime())) return 'Just now';
  return new Intl.DateTimeFormat(undefined, { month: 'short', day: 'numeric' }).format(date);
}

function LinkIllustration() {
  return (
    <div className="hero-art" role="img" aria-label="A long address becomes a short, shareable link">
      <div className="art-orbit orbit-one" />
      <div className="art-orbit orbit-two" />
      <div className="art-sticker sticker-top"><span className="sticker-star">✳</span> ready when you are</div>
      <div className="link-demo">
        <div className="demo-topline"><span className="live-dot" /> LINK TRANSFORMER <span className="demo-menu">···</span></div>
        <div className="demo-source">
          <span className="demo-icon source-icon">↗</span>
          <div className="demo-text"><small>THE LONG VERSION</small><span>yourbrand.com/campaign/<br />summer-drop?source=everywhere</span></div>
          <span className="demo-fade">•••</span>
        </div>
        <div className="transform-line"><span /><span className="transform-node">↓</span><span /></div>
        <div className="demo-result">
          <div className="demo-result-icon">p<span>.</span></div>
          <div className="demo-text"><small>THE PLINK VERSION</small><strong>plink<span>.link</span>/go</strong></div>
          <span className="demo-copy-icon" aria-hidden="true"><svg viewBox="0 0 20 20" fill="none"><rect x="7" y="7" width="9" height="10" rx="2" stroke="currentColor" strokeWidth="1.5" /><path d="M13 7V5a2 2 0 0 0-2-2H5a2 2 0 0 0-2 2v7a2 2 0 0 0 2 2h2" stroke="currentColor" strokeWidth="1.5" /></svg></span>
        </div>
        <div className="demo-footer"><span className="footer-check">✓</span> SHORT, SWEET, AND YOURS <span className="footer-sparkle">✳</span></div>
      </div>
      <div className="art-sticker sticker-bottom"><span className="sparkle-icon">✳</span><span>One link.<br /><b>Anywhere.</b></span></div>
      <div className="art-caption"><span className="caption-line" /> THE INTERNET, WITH LESS CLUTTER</div>
    </div>
  );
}

function FeatureCard({ number, icon, tone, title, children }) {
  return (
    <article className="feature-card">
      <span className="feature-number">{number}</span>
      <div className={`feature-icon ${tone}`} aria-hidden="true">{icon}</div>
      <h3>{title}</h3>
      <p>{children}</p>
    </article>
  );
}

function App() {
  const [originalUrl, setOriginalUrl] = useState('');
  const [email, setEmail] = useState('');
  const [errors, setErrors] = useState({});
  const [isLoading, setIsLoading] = useState(false);
  const [result, setResult] = useState(null);
  const [recent, setRecent] = useState(readRecent);
  const [copiedId, setCopiedId] = useState('');
  const [toast, setToast] = useState('');
  const resultRef = useRef(null);
  const toastTimer = useRef(null);
  const copyTimer = useRef(null);

  useEffect(() => () => {
    window.clearTimeout(toastTimer.current);
    window.clearTimeout(copyTimer.current);
  }, []);

  useEffect(() => {
    if (result) resultRef.current?.scrollIntoView({ behavior: 'smooth', block: 'nearest' });
  }, [result]);

  function announce(message) {
    window.clearTimeout(toastTimer.current);
    setToast(message);
    toastTimer.current = window.setTimeout(() => setToast(''), 2800);
  }

  async function copyLink(value, id) {
    try {
      try {
        await navigator.clipboard.writeText(value);
      } catch {
        const temporary = document.createElement('textarea');
        temporary.value = value;
        temporary.setAttribute('readonly', '');
        temporary.style.position = 'fixed';
        temporary.style.opacity = '0';
        document.body.append(temporary);
        temporary.select();
        const copied = document.execCommand('copy');
        temporary.remove();
        if (!copied) throw new Error('Clipboard unavailable');
      }
      setCopiedId(id);
      window.clearTimeout(copyTimer.current);
      copyTimer.current = window.setTimeout(() => setCopiedId(''), 1500);
      announce('Short link copied to clipboard.');
    } catch {
      announce('Clipboard access is unavailable. Open the short link and copy it from the address bar.');
    }
  }

  async function handleSubmit(event) {
    event.preventDefault();
    setResult(null);

    const nextErrors = { url: validateUrl(originalUrl), email: validateEmail(email) };
    setErrors(nextErrors);
    if (nextErrors.url || nextErrors.email) {
      document.getElementById(nextErrors.url ? 'original-url' : 'email')?.focus();
      return;
    }

    setIsLoading(true);
    try {
      const response = await fetch(`${API_BASE_URL}/api/shortenURL`, {
        method: 'POST',
        headers: { 'Content-Type': 'application/json', Accept: 'application/json' },
        body: JSON.stringify({ originalUrl: originalUrl.trim(), email: email.trim() })
      });
      const payload = await response.json().catch(() => null);
      if (!response.ok) throw new Error(errorMessage(payload, response.status));
      if (!payload?.shortCode) throw new Error('The service returned an incomplete link. Please try again.');

      const entry = {
        shortUrl: `${API_BASE_URL || window.location.origin}/api/redirectURL/${encodeURIComponent(payload.shortCode)}`,
        originalUrl: payload.originalUrl || originalUrl.trim(),
        shortCode: payload.shortCode,
        createdAt: new Date().toISOString()
      };
      setResult(entry);

      const updated = [entry, ...recent.filter((item) => item.shortCode !== entry.shortCode)].slice(0, 5);
      setRecent(updated);
      let saved = true;
      try {
        localStorage.setItem(STORAGE_KEY, JSON.stringify(updated));
      } catch {
        saved = false;
      }
      announce(saved
        ? 'Your short link is ready.'
        : 'Short link ready. Browser storage is unavailable, so it won’t appear in your recent list.');
    } catch (error) {
      announce(error instanceof TypeError
        ? 'Could not reach the service. Check that it’s running and try again.'
        : error.message);
    } finally {
      setIsLoading(false);
    }
  }

  function updateField(field, value) {
    if (field === 'url') setOriginalUrl(value);
    else setEmail(value);
    if (errors[field]) setErrors((current) => ({ ...current, [field]: '' }));
  }

  return (
    <div className="page-shell">
      <header className="topbar">
        <a className="brand" href="#top" aria-label="plink home"><LinkMark /><span>plink<span className="brand-period">.</span></span></a>
        <nav className="main-nav" aria-label="Main navigation">
          <a href="#how-it-works">How it works</a>
          <a href="#your-links">Your links</a>
        </nav>
        <a className="topbar-cta" href="#shorten-form">Make a short link <span aria-hidden="true">↗</span></a>
      </header>

      <main id="top">
        <section className="hero" aria-labelledby="hero-title">
          <div className="hero-copy">
            <div className="eyebrow"><span className="eyebrow-dot" /> A little link goes a long way</div>
            <h1 id="hero-title">Long links.<br /><span>Little footprint.</span></h1>
            <p className="hero-description">Make your URLs easier to share, easier to remember, and ready to go wherever you do.</p>

            <form className="shorten-card" id="shorten-form" onSubmit={handleSubmit} noValidate>
              <div className="field-group">
                <label htmlFor="original-url">Your destination URL</label>
                <div className={`input-wrap${errors.url ? ' invalid' : ''}`}>
                  <ChainIcon className="field-icon" />
                  <input id="original-url" name="originalUrl" type="url" inputMode="url" placeholder="https://your-very-long-link.com/..." autoComplete="url" required value={originalUrl} onChange={(event) => updateField('url', event.target.value)} aria-invalid={Boolean(errors.url)} aria-describedby="url-hint url-error" />
                </div>
                <p className="field-hint" id="url-hint">Paste the full link, including https://</p>
                <p className="field-error" id="url-error" aria-live="polite">{errors.url || ''}</p>
              </div>

              <div className="field-group email-field">
                <label htmlFor="email">Send my link to</label>
                <div className={`input-wrap${errors.email ? ' invalid' : ''}`}>
                  <MailIcon className="field-icon" />
                  <input id="email" name="email" type="email" inputMode="email" placeholder="you@example.com" autoComplete="email" required value={email} onChange={(event) => updateField('email', event.target.value)} aria-invalid={Boolean(errors.email)} aria-describedby="email-hint email-error" />
                </div>
                <p className="field-hint" id="email-hint">We’ll email you a copy when it’s ready.</p>
                <p className="field-error" id="email-error" aria-live="polite">{errors.email || ''}</p>
              </div>

              <button className={`submit-button${isLoading ? ' is-loading' : ''}`} id="submit-button" type="submit" disabled={isLoading}>
                <span className="button-label">{isLoading ? 'Making your link' : 'Make it shorter'}</span>
                {!isLoading && <ArrowIcon className="button-arrow" />}
              </button>
              <p className="form-note"><ShieldIcon /> Simple, secure, and ready to share.</p>
            </form>

            {result && (
              <section className="result-card" ref={resultRef} aria-live="polite" aria-atomic="true">
                <div className="result-heading"><span className="success-mark" aria-hidden="true">✓</span><div><p className="result-kicker">Your short link is ready</p><p className="result-original" title={result.originalUrl}>{result.originalUrl}</p></div></div>
                <div className="result-link-row">
                  <a className="result-link" href={result.shortUrl} target="_blank" rel="noopener noreferrer">{result.shortUrl}</a>
                  <button className="copy-button" type="button" onClick={() => copyLink(result.shortUrl, 'result')}><span className="copy-label">{copiedId === 'result' ? 'Copied!' : 'Copy'}</span></button>
                </div>
                <p className="result-footnote">A copy is on its way to your inbox.</p>
              </section>
            )}
          </div>

          <LinkIllustration />
        </section>

        <section className="recent-section" id="your-links" aria-labelledby="recent-title">
          <div className="section-heading"><div><p className="section-eyebrow">YOUR LITTLE LINK LIBRARY</p><h2 id="recent-title">Made recently</h2></div><span className="local-note">Saved on this device</span></div>
          {recent.length > 0 ? (
            <div className="recent-list">
              {recent.map((entry) => (
                <article className="recent-item" key={entry.shortCode || entry.shortUrl}>
                  <span className="recent-symbol" aria-hidden="true">↗</span>
                  <div className="recent-main">
                    <a className="recent-short" href={entry.shortUrl} target="_blank" rel="noopener noreferrer">{entry.shortUrl}</a>
                    <span className="recent-original" title={entry.originalUrl}>{entry.originalUrl}</span>
                  </div>
                  <span className="recent-date">{formatDate(entry.createdAt)}</span>
                  <button className="recent-copy" type="button" onClick={() => copyLink(entry.shortUrl, entry.shortCode || entry.shortUrl)} aria-label={`Copy ${entry.shortUrl}`}>
                    {copiedId === (entry.shortCode || entry.shortUrl) ? 'Copied!' : 'Copy'}
                  </button>
                </article>
              ))}
            </div>
          ) : (
            <div className="empty-state"><div className="empty-icon" aria-hidden="true">↗</div><p>Your links will land here.</p><span>Create your first one above and it’ll be easy to find again.</span></div>
          )}
        </section>

        <section className="how-section" id="how-it-works" aria-labelledby="how-title">
          <div className="how-heading"><p className="section-eyebrow">THOUGHTFULLY SHORTENED</p><h2 id="how-title">Good links make<br />good things happen.</h2></div>
          <div className="feature-grid">
            <FeatureCard number="01" icon="↗" tone="feature-icon-lilac" title="Ready in a moment">Drop in a destination and get a clean, shareable link back in seconds.</FeatureCard>
            <FeatureCard number="02" icon="⌁" tone="feature-icon-lime" title="Built to keep moving">Redis caching and a scalable service help your redirects stay quick.</FeatureCard>
            <FeatureCard number="03" icon="✉" tone="feature-icon-peach" title="A little inbox magic">Your new link is also sent to your email, so it’s never far away.</FeatureCard>
          </div>
        </section>
      </main>

      <footer className="footer"><a className="brand footer-brand" href="#top"><LinkMark small /><span>plink<span className="brand-period">.</span></span></a><span>Make room for the good stuff.</span><a className="back-top" href="#top">Back to top ↑</a></footer>
      <div className={`toast${toast ? ' visible' : ''}`} role="status" aria-live="polite">{toast}</div>
    </div>
  );
}

export default App;
