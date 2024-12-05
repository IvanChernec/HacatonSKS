public class DjangoApiService
{
    private readonly HttpClient _httpClient;

    public DjangoApiService(IHttpClientFactory httpClientFactory)
    {
        _httpClient = httpClientFactory.CreateClient("DjangoAPI");
    }

    public async Task<List<Group>> GetGroupsAsync()
    {
        var response = await _httpClient.GetAsync("groups/");
        response.EnsureSuccessStatusCode();
        return await response.Content.ReadFromJsonAsync<List<Group>>();
    }

    public async Task<List<Teacher>> GetTeachersAsync()
    {
        var response = await _httpClient.GetAsync("teachers/");
        response.EnsureSuccessStatusCode();
        return await response.Content.ReadFromJsonAsync<List<Teacher>>();
    }

    public async Task<List<Subject>> GetSubjectsAsync()
    {
        var response = await _httpClient.GetAsync("subjects/");
        response.EnsureSuccessStatusCode();
        return await response.Content.ReadFromJsonAsync<List<Subject>>();
    }

    /*public async Task<List<Schedule>> GetSchedulesAsync()
    {
        var response = await _httpClient.GetAsync("schedules/");
        response.EnsureSuccessStatusCode();
        return await response.Content.ReadFromJsonAsync<List<Schedule>>();
    }*/
}