import type { ReactNode } from 'react';

interface PageHeaderProps {
  kicker?: string;
  title: string;
  subtitle?: string;
  action?: ReactNode;
  badge?: string;
}

export default function PageHeader({ kicker, title, subtitle, action, badge }: PageHeaderProps) {
  return (
    <div style={{
      background: 'linear-gradient(160deg, #f0fdf4 0%, #ffffff 55%)',
      borderRadius: 'var(--radius-lg)',
      border: '1px solid rgba(187, 247, 208, 0.55)',
      borderTop: '2px solid var(--color-brand)',
      padding: '14px 22px',
      display: 'flex',
      alignItems: 'center',
      justifyContent: 'space-between',
      gap: 16,
      marginBottom: 'var(--space-6)',
      boxShadow: '0 1px 6px rgba(22, 163, 74, 0.07), 0 1px 2px rgba(0, 0, 0, 0.04)',
    }}>

      {/* Left: kicker + title + subtitle */}
      <div style={{ minWidth: 0 }}>
        {kicker && (
          <div style={{
            fontSize: 10,
            fontWeight: 700,
            letterSpacing: 1,
            textTransform: 'uppercase' as const,
            color: 'var(--color-brand)',
            marginBottom: 3,
          }}>
            {kicker}
          </div>
        )}
        <div style={{
          fontWeight: 700,
          fontSize: 'var(--text-md)',
          color: 'var(--color-text)',
          letterSpacing: -0.3,
          lineHeight: 1.15,
        }}>
          {title}
        </div>
        {subtitle && (
          <div style={{
            fontSize: 'var(--text-sm)',
            color: 'var(--color-text-muted)',
            marginTop: 3,
            lineHeight: 1.4,
          }}>
            {subtitle}
          </div>
        )}
      </div>

      {/* Right: badge + action */}
      {(badge || action) && (
        <div style={{ display: 'flex', alignItems: 'center', gap: 10, flexShrink: 0 }}>
          {badge && (
            <span style={{
              background: 'rgba(22, 163, 74, 0.08)',
              color: 'var(--color-brand-dark)',
              border: '1px solid rgba(22, 163, 74, 0.18)',
              borderRadius: 'var(--radius-sm)',
              padding: '3px 9px',
              fontSize: 10,
              fontWeight: 700,
              letterSpacing: 0.7,
              textTransform: 'uppercase' as const,
            }}>
              {badge}
            </span>
          )}
          {action}
        </div>
      )}
    </div>
  );
}
