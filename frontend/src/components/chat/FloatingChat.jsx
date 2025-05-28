'use client'

import {useEffect, useRef, useState} from "react";
import {useChat} from 'ai/react'
import Markdown from "react-markdown";
import {useSearchParams} from "next/navigation";

export default function FloatingChat() {
  const { messages, input, isLoading, handleInputChange, append } = useChat();
  const [isOpen, setIsOpen] = useState(false);
  const [streamingMessage, setStreamingMessage] = useState('');
  const [isWaitingForResponse, setIsWaitingForResponse] = useState(false);
  const messagesEndRef = useRef(null);
  const searchParams = useSearchParams();
  const torName = searchParams.get('torName');

  const scrollToBottom = () => {
    messagesEndRef.current?.scrollIntoView({ behavior: "smooth" });
  };

  useEffect(() => {
    scrollToBottom();
  }, [messages, streamingMessage]);

  async function handleSubmit(e) {
    e.preventDefault();
    if (!input.trim()) return;

    const userMessage = { role: 'user', content: input };

    await append(userMessage);
    setIsWaitingForResponse(true);

    const parseRequirementResponse = (responseText) => {
      try {
        // Check if it's the AssistantMessage format
        if (responseText.includes('AssistantMessage [')) {
          // Extract textContent using regex
          const textContentMatch = responseText.match(/textContent=([^,]+(?:,[^,]*)*?)(?:,\s*metadata=|$)/s);
          if (textContentMatch) {
            let textContent = textContentMatch[1];

            // Remove leading/trailing quotes if present
            if (textContent.startsWith('"') && textContent.endsWith('"')) {
              textContent = textContent.slice(1, -1);
            }

            // Handle escaped characters
            textContent = textContent
                .replace(/\\n/g, '\n')
                .replace(/\\t/g, '\t')
                .replace(/\\"/g, '"')
                .replace(/\\\\/g, '\\');

            return {
              success: true,
              content: textContent,
              messageType: 'ASSISTANT',
              rawResponse: responseText
            };
          }
        }

        // Try to parse as JSON
        const parsed = JSON.parse(responseText);
        if (parsed.textContent) {
          return {
            success: true,
            content: parsed.textContent,
            messageType: parsed.messageType || 'ASSISTANT',
            rawResponse: responseText
          };
        }

        return {
          success: true,
          content: responseText,
          messageType: 'ASSISTANT',
          rawResponse: responseText
        };
      } catch {
        // If not JSON, treat as plain text
        return {
          success: true,
          content: responseText,
          messageType: 'ASSISTANT',
          rawResponse: responseText
        };
      }
    };


    try {
      const response = await fetch(`${process.env.NEXT_PUBLIC_BACKEND_URL}/ask?source=${torName}&question=${input}`, {
        method: 'GET',
        headers: { 'Content-Type': 'application/json' },
      });
      console.log(response);

      if (!response.ok) {
        throw new Error(`HTTP error! status: ${response.status}`);
      }

      const responseText = await response.text();
      console.log(`Response text for ${torName}:`, responseText);

      const parsedResult = parseRequirementResponse(responseText);
      setIsWaitingForResponse(false);

      // const reader = response.body?.getReader();
      // if (!reader) {
      //   setIsWaitingForResponse(false);
      //   return;
      // }
      //
      // const decoder = new TextDecoder();
      // let assistantMessageContent = '';
      //
      // while (true) {
      //   const { done, value } = await reader.read();
      //   if (done) {
      //     setIsWaitingForResponse(false);
      //     break;
      //   }
      //
      //   const chunk = decoder.decode(value);
      //   const lines = chunk.split('\n');
      //
      //   for (const line of lines) {
      //     if (line.startsWith('data: ')) {
      //       try {
      //         const jsonString = line.replace('data: ', '');
      //         const messageData = JSON.parse(jsonString);
      //         assistantMessageContent += messageData.content;
      //       } catch (error) {
      //         console.error('Error parsing SSE data:', error);
      //       }
      //     }
      //   }
      //
      //   setStreamingMessage(assistantMessageContent);
      // }

      await append({ role: 'assistant', content: parsedResult?.content || 'Not found anything.' });
      setStreamingMessage('');
    } catch (error) {
      console.error('Error:', error);
      setIsWaitingForResponse(false);
    }
  }


  // Simple spinner component with inline styles instead of Tailwind classes
  const Spinner = ({ size = "24px" }) => (
    <div
      style={{
        width: size,
        height: size,
        borderRadius: '50%',
        border: '2px solid white',
        borderTopColor: 'transparent',
        animation: 'spin 1s linear infinite'
      }}
    />
  );

  // Add the keyframes animation to the document
  useEffect(() => {
    // Check if the animation style is already added
    if (!document.getElementById('spinner-animation')) {
      const styleElement = document.createElement('style');
      styleElement.id = 'spinner-animation';
      styleElement.textContent = `
        @keyframes spin {
          to { transform: rotate(360deg); }
        }
      `;
      document.head.appendChild(styleElement);
    }

    return () => {
      // Clean up when component unmounts
      const styleElement = document.getElementById('spinner-animation');
      if (styleElement) {
        styleElement.remove();
      }
    };
  }, []);


  return (
    <>
      {/* Floating Chat Button */}
      <button
        onClick={() => setIsOpen(true)}
        style={{
          position: 'fixed',
          bottom: '24px',
          right: '24px',
          backgroundColor: '#2563eb',
          color: 'white',
          borderRadius: '9999px',
          padding: '12px 16px',
          boxShadow: '0 4px 12px rgba(0,0,0,0.2)',
          zIndex: 0
        }}
      >
        💬
      </button>

      {/* Chat Box Modal */}
      {isOpen && (
        <div
          style={{
            position: 'fixed',
            bottom: '100px',
            right: '24px',
            width: '360px',
            maxHeight: '80vh',
            backgroundColor: 'white',
            border: '1px solid #ddd',
            borderRadius: '12px',
            display: 'flex',
            flexDirection: 'column',
            zIndex: 9999,
            boxShadow: '0 8px 16px rgba(0,0,0,0.2)'
          }}
        >
          <div style={{ padding: '12px', borderBottom: '1px solid #eee', display: 'flex', justifyContent: 'space-between' }}>
            <strong className="text-gray-800">AI Assistant</strong>
            <button onClick={() => setIsOpen(false)} style={{ background: 'none', border: 'none', fontSize: '16px' }}>✖</button>
          </div>
          <div style={{ flex: 1, overflowY: 'auto', padding: '12px' }} className="text-gray-800">
            {messages.map((m, i) => (
                <div key={i} style={{ marginBottom: '8px', textAlign: m.role === 'user' ? 'right' : 'left' }}>
                  <div
                      style={{
                        display: 'inline-block',
                        backgroundColor: m.role === 'user' ? '#dbeafe' : '#f3f4f6',
                        padding: '8px 12px',
                        borderRadius: '8px'
                      }}
                  >
                    <strong>{m.role === 'user' ? 'User' : 'Bot'}:</strong>
                    <Markdown>{m.content}</Markdown>
                  </div>
                </div>
            ))}


            {/* No separate spinner in the message area anymore */}

            {/* Show streaming message with "typing" indicator */}
            {streamingMessage && (
              <div className="flex justify-start">
                <div className="max-w-[80%] rounded-lg p-3 bg-gray-200 text-gray-800">
                  <p className="text-sm">{streamingMessage}</p>
                  <p className="text-xs mt-1 opacity-70">Typing...</p>
                </div>
              </div>
            )}
            <div ref={messagesEndRef} />
          </div>
          <form onSubmit={handleSubmit} style={{ display: 'flex', borderTop: '1px solid #eee', padding: '8px' }} className="text-gray-800">
            <input
              type="text"
              value={input}
              onChange={handleInputChange}
              placeholder="Ask something..."
              style={{ flex: 1, padding: '8px', borderRadius: '8px', border: '1px solid #ddd', marginRight: '8px' }}
              disabled={isWaitingForResponse || streamingMessage}
            />
            <button
              type="submit"
              disabled={isWaitingForResponse || streamingMessage || !input.trim()}
              style={{
                padding: '8px 12px',
                backgroundColor: '#2563eb',
                color: 'white',
                borderRadius: '8px',
                width: '80px',
                display: 'flex',
                justifyContent: 'center',
                alignItems: 'center'
              }}
            >
              {(isWaitingForResponse || streamingMessage) ? <Spinner size="20px" />  : 'Send'}
            </button>
          </form>
        </div>
      )}
    </>
  )
}