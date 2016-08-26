# AndroidToSphere
App used to connect from mobile to SphereSvr to handle GM Pages and some more things (in the future)<br><br>


To make it work these changes must be made on the server's source:

1) Search for enum CONNECT_TYPE and add this to it: CONNECT_ANDROID,	// Mobile device connected

2) Inside CServer:ListClients(), search for the switch ( pClient->GetConnectType() ) and add this code inside:
				case CONNECT_ANDROID:<br>
					pszState = "ANDROID";<br>
					break;<br>

3) Inside CClient::SysMessage() search for switch ( GetConnectType() ) and add 'case CONNECT_ANDROID:' in the same section as CONNECT_TELNET and CONNECT_AXIS

4) Open CClient.h and add the method: bool OnRxMobileApp(const byte * pData, size_t len); near of bool OnRxAxis( const byte * pData, size_t len );

5) Search for CClient::IsConnecting() and add case CONNECT_ANDROID: inside the switch, before return false;

6) Search for the function under the last one: CClient:GetConnectTypeStr() and add this: case CONNECT_ANDROID:	return "Android"; inside the switch.

7) Search for NetworkInput::ProcessData() and search for this line: if ((client->GetConnectType() != CONNECT_TELNET) && (client->GetConnectType() != CONNECT_AXIS)) 
and add this code inside the if: && (client->GetConnectType() != CONNECT_ANDROID)

8) Search for this method: bool NetworkInput::processOtherClientData(NetState* state, Packet* buffer)
    Move inside switch (client->GetConnectType()) and add this code:<br>
      case CONNECT_ANDROID:<br>
			EXC_SET("android message");<br>
			if (client->OnRxMobileApp(buffer->getRemainingData(), buffer->getRemainingLength()) == false)<br>
				return false;<br>
			break;<br>

9) Search for bool CClient::OnRxPing( const byte * pData, size_t iLen )  and add the following code inside switch ( pData[0] ):
  case 0x6D:	// 'm' for Mobile connections.
		{
		
			g_Log.Event(LOGM_CLIENTS_LOG | LOGL_EVENT, "%x:Android connection requested from %s\n", GetSocketID(), GetPeerStr());
			SetConnectType(CONNECT_ANDROID);
			m_zLogin[0] = 0;
			SysMessagef("%s %s Admin Telnet\n", g_Cfg.GetDefaultMsg(DEFMSG_CONSOLE_WELCOME_1), g_Serv.GetName());

			if (g_Cfg.m_fLocalIPAdmin)
			{
				// don't bother logging in if local.

				if (GetPeer().IsLocalAddr())
				{
					CAccountRef pAccount = g_Accounts.Account_Find("Administrator");
					if (!pAccount)
						pAccount = g_Accounts.Account_Find("RemoteAdmin");
					if (pAccount)
					{
						CSString sMsg;
						byte lErr = LogIn(pAccount, sMsg);
						if (lErr != PacketLoginError::Success)
						{
							if (lErr != PacketLoginError::Invalid)
								SysMessage(sMsg);
							return false;
						}
						return OnRxConsoleLoginComplete();
					}
				}
			}
			return true;
		}

10) add this whole method above the function tweaked last:

bool CClient::OnRxMobileApp(const byte * pData, size_t iLen)
{

	ADDTOCALLSTACK("CClient::OnRxMobileApp");
	if (!iLen || (GetConnectType() != CONNECT_ANDROID))
		return false;

	while (iLen--)
	{
		int iRet = OnConsoleKey(m_Targ_Text, *pData++, GetAccount() != NULL);
		if (!iRet)
			return false;
	}

	if (GetAccount() == NULL)
	{
		tchar** pszAcc;
		tchar* pszArgs;
		strcpy(pszArgs, m_Targ_Text);
		Str_ParseCmds(pszArgs, pszAcc, 2, ",");
		if (!m_zLogin[0])
		{
			if ((uint)(strlen(m_Targ_Text)) <= (CountOf(m_zLogin) - 1))
			{
				strcpy(m_zLogin, m_Targ_Text);
				m_Targ_Text.Empty();
				return true;
			}
			m_Targ_Text.Empty();
			return false;
		}
		CSString sMsg;

		CAccountRef pAccount = g_Accounts.Account_Find(m_zLogin);
		if ((pAccount == NULL) || (pAccount->GetPrivLevel() < PLEVEL_Counsel))
		{
			SysMessagef("\"MSG:%s\"", g_Cfg.GetDefaultMsg(DEFMSG_AXIS_NOT_PRIV));
			m_Targ_Text.Empty();
			return false;
		}
		if (LogIn(m_zLogin, m_Targ_Text, sMsg) == PacketLoginError::Success)
		{
			if (GetPrivLevel() < PLEVEL_Counsel)
			{
				SysMessagef("\"MSG:%s\"", g_Cfg.GetDefaultMsg(DEFMSG_AXIS_NOT_PRIV));
				return false;
			}
			if (GetPeer().IsValidAddr())
			{
				CScriptTriggerArgs Args;
				Args.m_VarsLocal.SetStrNew("Account", GetName());
				Args.m_VarsLocal.SetStrNew("IP", GetPeer().GetAddrStr());
				TRIGRET_TYPE tRet = TRIGRET_RET_DEFAULT;
				r_Call("f_mobile_preload", this, &Args, NULL, &tRet);
				if (tRet == TRIGRET_RET_FALSE)	// return 0 = clean stop
					return false;
				if (tRet == TRIGRET_RET_TRUE)	// return 1 = stop + predefined message.
				{
					SysMessagef("\"MSG:%s\"", g_Cfg.GetDefaultMsg(DEFMSG_AXIS_DENIED));
					return false;
				}
				return true;
			}
			return false;
		}
		else if (!sMsg.IsEmpty())
		{
			SysMessagef("\"MSG:%s\"", (lpctstr)sMsg);
			return false;
		}
		SysMessage("Logged in");
		return true;
	}
	else {
		CChar * pChar = GetAccount()->m_uidLastChar.CharFind();
		if (!pChar)
			pChar = GetAccount()->m_Chars.GetChar(0).CharFind();
		if (!pChar)
		{
			SysMessage("You have no character to log with");
			return false;
		}
	}
	return true;
}
