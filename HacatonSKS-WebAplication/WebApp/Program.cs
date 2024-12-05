using Microsoft.AspNetCore.Builder;
using MudBlazor.Services;
using WebApp.Components;

var builder = WebApplication.CreateBuilder(args);

// Add services to the container.
builder.Services.AddRazorComponents()
    .AddInteractiveServerComponents();

// Add MudBlazor services
builder.Services.AddMudServices();

// Add HTTP client for Django API
builder.Services.AddHttpClient("DjangoAPI", client =>
{
    client.BaseAddress = new Uri("http://192.168.64.25/");
});

// Add custom services
builder.Services.AddTransient<DjangoApiService>();

var app = builder.Build();

// Configure the HTTP request pipeline.
if (!app.Environment.IsDevelopment())
{
    app.UseExceptionHandler("/Error", createScopeForErrors: true);
    app.UseHsts();
}

app.UseHttpsRedirection();
app.UseStaticFiles();
app.UseRouting();
app.UseAntiforgery();

app.MapRazorComponents<App>()
    .AddInteractiveServerRenderMode();

app.Run();