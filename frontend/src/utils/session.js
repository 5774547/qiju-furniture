const SESSION_KEY = 'qiju_session_id'

export function getSessionId() {
  let sessionId = localStorage.getItem(SESSION_KEY)
  if (!sessionId) {
    sessionId = crypto.randomUUID()
    localStorage.setItem(SESSION_KEY, sessionId)
  }
  return sessionId
}

export function clearSessionId() {
  localStorage.removeItem(SESSION_KEY)
}
