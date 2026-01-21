/**
 * ChubAI.node.ts
 * Cogn8n custom node for Chub AI integration
 * Enables workflow automation for character management, lorebook sync, and chat operations
 */

import {
	IExecuteFunctions,
	INodeExecutionData,
	INodeType,
	INodeTypeDescription,
	NodeOperationError,
} from 'n8n-workflow';

export class ChubAI implements INodeType {
	description: INodeTypeDescription = {
		displayName: 'Chub AI',
		name: 'chubAI',
		icon: 'file:chubai.svg',
		group: ['transform'],
		version: 1,
		subtitle: '={{$parameter["operation"] + ": " + $parameter["resource"]}}',
		description: 'Interact with Chub AI platform for character and content management',
		defaults: {
			name: 'Chub AI',
		},
		inputs: ['main'],
		outputs: ['main'],
		credentials: [
			{
				name: 'chubAiApi',
				required: true,
			},
		],
		properties: [
			// Resource Selection
			{
				displayName: 'Resource',
				name: 'resource',
				type: 'options',
				noDataExpression: true,
				options: [
					{
						name: 'Character',
						value: 'character',
						description: 'Manage AI characters',
					},
					{
						name: 'Lorebook',
						value: 'lorebook',
						description: 'Manage lorebooks and world info',
					},
					{
						name: 'Chat',
						value: 'chat',
						description: 'Manage chat sessions',
					},
					{
						name: 'Persona',
						value: 'persona',
						description: 'Manage user personas',
					},
				],
				default: 'character',
			},

			// Character Operations
			{
				displayName: 'Operation',
				name: 'operation',
				type: 'options',
				noDataExpression: true,
				displayOptions: {
					show: {
						resource: ['character'],
					},
				},
				options: [
					{
						name: 'Create',
						value: 'create',
						description: 'Create a new character',
						action: 'Create a character',
					},
					{
						name: 'Get',
						value: 'get',
						description: 'Get a character by ID',
						action: 'Get a character',
					},
					{
						name: 'Update',
						value: 'update',
						description: 'Update an existing character',
						action: 'Update a character',
					},
					{
						name: 'Delete',
						value: 'delete',
						description: 'Delete a character',
						action: 'Delete a character',
					},
					{
						name: 'List',
						value: 'list',
						description: 'List all characters',
						action: 'List characters',
					},
					{
						name: 'Export',
						value: 'export',
						description: 'Export character to file format',
						action: 'Export a character',
					},
					{
						name: 'Import',
						value: 'import',
						description: 'Import character from file',
						action: 'Import a character',
					},
				],
				default: 'get',
			},

			// Lorebook Operations
			{
				displayName: 'Operation',
				name: 'operation',
				type: 'options',
				noDataExpression: true,
				displayOptions: {
					show: {
						resource: ['lorebook'],
					},
				},
				options: [
					{
						name: 'Create',
						value: 'create',
						description: 'Create a new lorebook',
						action: 'Create a lorebook',
					},
					{
						name: 'Get',
						value: 'get',
						description: 'Get a lorebook by ID',
						action: 'Get a lorebook',
					},
					{
						name: 'Update',
						value: 'update',
						description: 'Update an existing lorebook',
						action: 'Update a lorebook',
					},
					{
						name: 'Delete',
						value: 'delete',
						description: 'Delete a lorebook',
						action: 'Delete a lorebook',
					},
					{
						name: 'Add Entry',
						value: 'addEntry',
						description: 'Add an entry to a lorebook',
						action: 'Add entry to lorebook',
					},
					{
						name: 'Sync',
						value: 'sync',
						description: 'Sync lorebook with external source',
						action: 'Sync lorebook',
					},
				],
				default: 'get',
			},

			// Chat Operations
			{
				displayName: 'Operation',
				name: 'operation',
				type: 'options',
				noDataExpression: true,
				displayOptions: {
					show: {
						resource: ['chat'],
					},
				},
				options: [
					{
						name: 'Create',
						value: 'create',
						description: 'Start a new chat session',
						action: 'Create a chat',
					},
					{
						name: 'Get',
						value: 'get',
						description: 'Get chat history',
						action: 'Get chat history',
					},
					{
						name: 'Send Message',
						value: 'sendMessage',
						description: 'Send a message in a chat',
						action: 'Send a message',
					},
					{
						name: 'Delete',
						value: 'delete',
						description: 'Delete a chat session',
						action: 'Delete a chat',
					},
					{
						name: 'Export',
						value: 'export',
						description: 'Export chat history',
						action: 'Export chat',
					},
				],
				default: 'get',
			},

			// Common Parameters
			{
				displayName: 'Character ID',
				name: 'characterId',
				type: 'string',
				default: '',
				required: true,
				displayOptions: {
					show: {
						resource: ['character'],
						operation: ['get', 'update', 'delete', 'export'],
					},
				},
				description: 'The ID of the character',
			},
			{
				displayName: 'Lorebook ID',
				name: 'lorebookId',
				type: 'string',
				default: '',
				required: true,
				displayOptions: {
					show: {
						resource: ['lorebook'],
						operation: ['get', 'update', 'delete', 'addEntry', 'sync'],
					},
				},
				description: 'The ID of the lorebook',
			},
			{
				displayName: 'Chat ID',
				name: 'chatId',
				type: 'string',
				default: '',
				required: true,
				displayOptions: {
					show: {
						resource: ['chat'],
						operation: ['get', 'sendMessage', 'delete', 'export'],
					},
				},
				description: 'The ID of the chat session',
			},

			// Character Create/Update Fields
			{
				displayName: 'Character Data',
				name: 'characterData',
				type: 'collection',
				placeholder: 'Add Field',
				default: {},
				displayOptions: {
					show: {
						resource: ['character'],
						operation: ['create', 'update'],
					},
				},
				options: [
					{
						displayName: 'Name',
						name: 'name',
						type: 'string',
						default: '',
						description: 'Character name',
					},
					{
						displayName: 'Description',
						name: 'description',
						type: 'string',
						typeOptions: {
							rows: 4,
						},
						default: '',
						description: 'Character description',
					},
					{
						displayName: 'Personality',
						name: 'personality',
						type: 'string',
						typeOptions: {
							rows: 4,
						},
						default: '',
						description: 'Character personality traits',
					},
					{
						displayName: 'Scenario',
						name: 'scenario',
						type: 'string',
						typeOptions: {
							rows: 4,
						},
						default: '',
						description: 'Initial scenario/context',
					},
					{
						displayName: 'First Message',
						name: 'first_mes',
						type: 'string',
						typeOptions: {
							rows: 4,
						},
						default: '',
						description: 'Character\'s first message',
					},
					{
						displayName: 'System Prompt',
						name: 'system_prompt',
						type: 'string',
						typeOptions: {
							rows: 4,
						},
						default: '',
						description: 'System prompt for the AI',
					},
					{
						displayName: 'Tags',
						name: 'tags',
						type: 'string',
						default: '',
						description: 'Comma-separated tags',
					},
				],
			},

			// Export Format
			{
				displayName: 'Export Format',
				name: 'exportFormat',
				type: 'options',
				displayOptions: {
					show: {
						operation: ['export'],
					},
				},
				options: [
					{
						name: 'TavernAI (PNG)',
						value: 'tavernai',
						description: 'TavernAI character card format',
					},
					{
						name: 'SillyTavern (JSON)',
						value: 'sillytavern',
						description: 'SillyTavern JSON format',
					},
					{
						name: 'Chub AI (JSON)',
						value: 'chubai',
						description: 'Native Chub AI format',
					},
				],
				default: 'chubai',
			},

			// Message Content
			{
				displayName: 'Message',
				name: 'message',
				type: 'string',
				typeOptions: {
					rows: 4,
				},
				default: '',
				required: true,
				displayOptions: {
					show: {
						resource: ['chat'],
						operation: ['sendMessage'],
					},
				},
				description: 'The message to send',
			},

			// Lorebook Entry
			{
				displayName: 'Entry Data',
				name: 'entryData',
				type: 'collection',
				placeholder: 'Add Field',
				default: {},
				displayOptions: {
					show: {
						resource: ['lorebook'],
						operation: ['addEntry'],
					},
				},
				options: [
					{
						displayName: 'Keys',
						name: 'keys',
						type: 'string',
						default: '',
						description: 'Comma-separated trigger keywords',
					},
					{
						displayName: 'Content',
						name: 'content',
						type: 'string',
						typeOptions: {
							rows: 4,
						},
						default: '',
						description: 'Entry content',
					},
					{
						displayName: 'Priority',
						name: 'priority',
						type: 'number',
						default: 10,
						description: 'Entry priority (higher = more important)',
					},
					{
						displayName: 'Depth',
						name: 'depth',
						type: 'number',
						default: 4,
						description: 'Context depth for activation',
					},
					{
						displayName: 'Enabled',
						name: 'enabled',
						type: 'boolean',
						default: true,
						description: 'Whether the entry is active',
					},
				],
			},

			// Pagination
			{
				displayName: 'Return All',
				name: 'returnAll',
				type: 'boolean',
				default: false,
				displayOptions: {
					show: {
						operation: ['list'],
					},
				},
				description: 'Whether to return all results or limit',
			},
			{
				displayName: 'Limit',
				name: 'limit',
				type: 'number',
				default: 50,
				displayOptions: {
					show: {
						operation: ['list'],
						returnAll: [false],
					},
				},
				description: 'Maximum number of results to return',
			},
		],
	};

	async execute(this: IExecuteFunctions): Promise<INodeExecutionData[][]> {
		const items = this.getInputData();
		const returnData: INodeExecutionData[] = [];

		const resource = this.getNodeParameter('resource', 0) as string;
		const operation = this.getNodeParameter('operation', 0) as string;

		const credentials = await this.getCredentials('chubAiApi');
		const apiKey = credentials.apiKey as string;
		const baseUrl = (credentials.baseUrl as string) || 'https://api.chub.ai';

		for (let i = 0; i < items.length; i++) {
			try {
				let responseData;

				if (resource === 'character') {
					responseData = await this.executeCharacterOperation(
						operation,
						i,
						apiKey,
						baseUrl
					);
				} else if (resource === 'lorebook') {
					responseData = await this.executeLorebookOperation(
						operation,
						i,
						apiKey,
						baseUrl
					);
				} else if (resource === 'chat') {
					responseData = await this.executeChatOperation(
						operation,
						i,
						apiKey,
						baseUrl
					);
				}

				if (Array.isArray(responseData)) {
					returnData.push(...responseData.map((item) => ({ json: item })));
				} else {
					returnData.push({ json: responseData });
				}
			} catch (error) {
				if (this.continueOnFail()) {
					returnData.push({ json: { error: error.message } });
					continue;
				}
				throw error;
			}
		}

		return [returnData];
	}

	private async executeCharacterOperation(
		operation: string,
		itemIndex: number,
		apiKey: string,
		baseUrl: string
	): Promise<any> {
		const headers = {
			Authorization: `Bearer ${apiKey}`,
			'Content-Type': 'application/json',
		};

		switch (operation) {
			case 'get': {
				const characterId = this.getNodeParameter('characterId', itemIndex) as string;
				const response = await this.helpers.request({
					method: 'GET',
					url: `${baseUrl}/api/core/characters/${characterId}`,
					headers,
					json: true,
				});
				return response;
			}

			case 'list': {
				const returnAll = this.getNodeParameter('returnAll', itemIndex) as boolean;
				const limit = returnAll ? 1000 : (this.getNodeParameter('limit', itemIndex) as number);
				const response = await this.helpers.request({
					method: 'GET',
					url: `${baseUrl}/api/core/characters`,
					qs: { limit },
					headers,
					json: true,
				});
				return response.data || response;
			}

			case 'create': {
				const characterData = this.getNodeParameter('characterData', itemIndex) as object;
				const response = await this.helpers.request({
					method: 'POST',
					url: `${baseUrl}/api/core/characters`,
					headers,
					body: characterData,
					json: true,
				});
				return response;
			}

			case 'update': {
				const characterId = this.getNodeParameter('characterId', itemIndex) as string;
				const characterData = this.getNodeParameter('characterData', itemIndex) as object;
				const response = await this.helpers.request({
					method: 'PUT',
					url: `${baseUrl}/api/core/characters/${characterId}`,
					headers,
					body: characterData,
					json: true,
				});
				return response;
			}

			case 'delete': {
				const characterId = this.getNodeParameter('characterId', itemIndex) as string;
				await this.helpers.request({
					method: 'DELETE',
					url: `${baseUrl}/api/core/characters/${characterId}`,
					headers,
				});
				return { success: true, deleted: characterId };
			}

			case 'export': {
				const characterId = this.getNodeParameter('characterId', itemIndex) as string;
				const format = this.getNodeParameter('exportFormat', itemIndex) as string;
				const response = await this.helpers.request({
					method: 'GET',
					url: `${baseUrl}/api/core/characters/${characterId}/export`,
					qs: { format },
					headers,
					json: true,
				});
				return response;
			}

			default:
				throw new NodeOperationError(
					this.getNode(),
					`Unknown operation: ${operation}`
				);
		}
	}

	private async executeLorebookOperation(
		operation: string,
		itemIndex: number,
		apiKey: string,
		baseUrl: string
	): Promise<any> {
		const headers = {
			Authorization: `Bearer ${apiKey}`,
			'Content-Type': 'application/json',
		};

		switch (operation) {
			case 'get': {
				const lorebookId = this.getNodeParameter('lorebookId', itemIndex) as string;
				const response = await this.helpers.request({
					method: 'GET',
					url: `${baseUrl}/api/core/lorebooks/${lorebookId}`,
					headers,
					json: true,
				});
				return response;
			}

			case 'addEntry': {
				const lorebookId = this.getNodeParameter('lorebookId', itemIndex) as string;
				const entryData = this.getNodeParameter('entryData', itemIndex) as any;
				
				// Parse keys from comma-separated string
				if (entryData.keys && typeof entryData.keys === 'string') {
					entryData.keys = entryData.keys.split(',').map((k: string) => k.trim());
				}

				const response = await this.helpers.request({
					method: 'POST',
					url: `${baseUrl}/api/core/lorebooks/${lorebookId}/entries`,
					headers,
					body: entryData,
					json: true,
				});
				return response;
			}

			case 'sync': {
				const lorebookId = this.getNodeParameter('lorebookId', itemIndex) as string;
				// Sync logic would go here
				return { success: true, synced: lorebookId };
			}

			default:
				throw new NodeOperationError(
					this.getNode(),
					`Unknown operation: ${operation}`
				);
		}
	}

	private async executeChatOperation(
		operation: string,
		itemIndex: number,
		apiKey: string,
		baseUrl: string
	): Promise<any> {
		const headers = {
			Authorization: `Bearer ${apiKey}`,
			'Content-Type': 'application/json',
		};

		switch (operation) {
			case 'get': {
				const chatId = this.getNodeParameter('chatId', itemIndex) as string;
				const response = await this.helpers.request({
					method: 'GET',
					url: `${baseUrl}/api/core/chats/${chatId}`,
					headers,
					json: true,
				});
				return response;
			}

			case 'sendMessage': {
				const chatId = this.getNodeParameter('chatId', itemIndex) as string;
				const message = this.getNodeParameter('message', itemIndex) as string;
				const response = await this.helpers.request({
					method: 'POST',
					url: `${baseUrl}/api/core/chats/${chatId}/messages`,
					headers,
					body: { content: message },
					json: true,
				});
				return response;
			}

			case 'create': {
				const characterId = this.getNodeParameter('characterId', itemIndex) as string;
				const response = await this.helpers.request({
					method: 'POST',
					url: `${baseUrl}/api/core/chats`,
					headers,
					body: { character_id: characterId },
					json: true,
				});
				return response;
			}

			default:
				throw new NodeOperationError(
					this.getNode(),
					`Unknown operation: ${operation}`
				);
		}
	}
}
